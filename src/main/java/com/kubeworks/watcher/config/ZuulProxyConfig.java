package com.kubeworks.watcher.config;

import com.google.common.collect.Maps;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.ZuulProxyAutoConfiguration;
import org.springframework.cloud.netflix.zuul.ZuulServerAutoConfiguration;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.util.RequestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableZuulProxy
@AutoConfigureBefore(value={ZuulServerAutoConfiguration.class, ZuulProxyAutoConfiguration.class})
@EnableConfigurationProperties(value={ServerProperties.class, ZuulProperties.class, MonitoringProperties.class})
public class ZuulProxyConfig {

    private final ZuulProperties zp;
    private final ServerProperties sp;
    private final MonitoringProperties mp;

    @Autowired
    public ZuulProxyConfig(final ZuulProperties zp, final ServerProperties sp, final MonitoringProperties mp) {
        this.zp = zp; this.sp = sp; this.mp = mp;
    }

    @Bean
    public SimpleRouteLocator customRouteLocator() {

        final LinkedHashMap<String, ZuulProperties.ZuulRoute> routes = Maps.newLinkedHashMap(zp.getRoutes());
        final LinkedHashMap<String, ZuulProperties.ZuulRoute> newRoute = mp.getClusters().entrySet().stream().flatMap(this::getRouteStream).collect(Collectors.toMap(ZuulProperties.ZuulRoute::getPath, Function.identity(), (k, v) -> v, LinkedHashMap::new));

        routes.putAll(newRoute);

        return new DefaultRouteLocator(sp.getServlet().getContextPath(), zp, routes);
    }

    private Stream<ZuulProperties.ZuulRoute> getRouteStream(final Map.Entry<String, MonitoringProperties.ClusterConfig> entry) {

        final String key = entry.getKey();
        final MonitoringProperties.ClusterConfig config = entry.getValue();
        final Stream.Builder<ZuulProperties.ZuulRoute> routeBuilder = Stream.builder();

        if (config.getPrometheus() != null && config.getPrometheus().isProxy()) {
            routeBuilder.add(getZuulRoute(key + "-prometheus", "/proxy/prometheus" + DiscoveryClientRouteLocator.DEFAULT_ROUTE, config.getPrometheus().getUrl()));
        }

        if (config.getLoki() != null && config.getLoki().isProxy()) {
            routeBuilder.add(getZuulRoute(key + "-loki", "/proxy/loki" + DiscoveryClientRouteLocator.DEFAULT_ROUTE, config.getLoki().getUrl()));
        }

        return routeBuilder.build();
    }

    private ZuulProperties.ZuulRoute getZuulRoute(final String key, final String path, final String url) {

        final ZuulProperties.ZuulRoute route = new ZuulProperties.ZuulRoute();

        route.setId(key); route.setPath(path); route.setUrl(url); route.setStripPrefix(true);

        return route;
    }

    @Slf4j
    public static class DefaultRouteLocator extends SimpleRouteLocator {

        private final AtomicReference<Map<String, ZuulProperties.ZuulRoute>> routes = new AtomicReference<>();
        private final String dispatcherServletPath;
        private final String zuulServletPath;
        private final PathMatcher pathMatcher = new AntPathMatcher();

        public DefaultRouteLocator(final String path, final ZuulProperties properties, final Map<String, ZuulProperties.ZuulRoute> routes) {

            super(path, properties); super.setOrder(-1);

            this.routes.set(routes);
            this.dispatcherServletPath = StringUtils.hasText(path) ? path : "/";
            this.zuulServletPath = properties.getServletPath();
        }

        @Override
        public List<Route> getRoutes() {
            return this.routes.get().values().stream()
                .map(zuulRoute -> {
                    try {
                        return getRoute(zuulRoute, zuulRoute.getPath());
                    } catch (Exception e) {
                        if (log.isWarnEnabled()) {
                            log.warn("Invalid route, routeId: {}, routeServiceId: {}, msg: {}", zuulRoute.getId(), zuulRoute.getServiceId(), e.getMessage());
                        }
                        if (log.isDebugEnabled()) { log.debug("", e); }
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        }

        @Override
        public Route getMatchingRoute(final String path) {

            final String adjustedPath = this.getAdjustPath(path);

            return super.getRoute(this.getZuulRoute(adjustedPath), adjustedPath);
        }

        protected String getAdjustPath(final String path) {

            String adjustedPath = path;

            if (RequestUtils.isDispatcherServletRequest() && StringUtils.hasText(this.dispatcherServletPath)) {
                if (!"/".equals(this.dispatcherServletPath) && path.startsWith(this.dispatcherServletPath)) {
                    adjustedPath = path.substring(this.dispatcherServletPath.length());
                    log.debug("Stripped dispatcherServletPath");
                }
            } else if (RequestUtils.isZuulServletRequest()) {
                if (StringUtils.hasText(this.zuulServletPath) && !"/".equals(this.zuulServletPath)) {
                    adjustedPath = path.substring(this.zuulServletPath.length());
                    log.debug("Stripped zuulServletPath");
                }
            } else {
                log.warn("do nothing");
            }

            log.debug("adjustedPath={}", adjustedPath);

            return adjustedPath;
        }

        @Override
        protected ZuulProperties.ZuulRoute getZuulRoute(final String adjustedPath) {

            if (matchesIgnoredPatterns(adjustedPath)) { return null; }

            return this.routes.get().entrySet().stream().filter(entry -> this.pathMatcher.match(entry.getKey(), adjustedPath)).findFirst().map(Map.Entry::getValue).orElse(null);
        }

        @Override
        protected void doRefresh() {
            log.warn("no nothing");
        }

        public void refreshRoutes(final Map<String, ZuulProperties.ZuulRoute> newRoutes) {

            routes.set(newRoutes);
            log.info("refresh zuul routes");

            if (log.isDebugEnabled()) { log.debug("newRoutes={}", newRoutes); }

            doRefresh();
        }
    }
}
