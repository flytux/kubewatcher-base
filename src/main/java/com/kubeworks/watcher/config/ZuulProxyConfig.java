package com.kubeworks.watcher.config;

import com.google.common.collect.Maps;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.ServerProperties;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@EnableZuulProxy
@Configuration
@AutoConfigureBefore(value = {ZuulServerAutoConfiguration.class, ZuulProxyAutoConfiguration.class})
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ZuulProxyConfig {

    private final ServerProperties server;
    private final ZuulProperties zuulProperties;
    private final MonitoringProperties monitoringProperties;

    @Bean
    public SimpleRouteLocator customRouteLocator() {

        Map<String, ZuulProperties.ZuulRoute> propertiesRoutes = zuulProperties.getRoutes();
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routes = Maps.newLinkedHashMap(propertiesRoutes);

        Map<String, MonitoringProperties.ClusterConfig> clusters = monitoringProperties.getClusters();

        LinkedHashMap<String, ZuulProperties.ZuulRoute> newRoute = clusters.entrySet().stream().flatMap(this::getRouteStream)
            .collect(Collectors.toMap(ZuulProperties.ZuulRoute::getPath, route -> route, (key, value) -> value, LinkedHashMap::new));
        routes.putAll(newRoute);

//        zuulProperties.setRoutes(routes);

        SimpleRouteLocator defaultRouteLocator = new DefaultRouteLocator(server.getServlet().getContextPath(), zuulProperties, routes);
        defaultRouteLocator.setOrder(-1);

        return defaultRouteLocator;
    }

    private Stream<ZuulProperties.ZuulRoute> getRouteStream(Map.Entry<String, MonitoringProperties.ClusterConfig> entry) {
        String key = entry.getKey();
        MonitoringProperties.ClusterConfig clusterConfig = entry.getValue();

        Stream.Builder<ZuulProperties.ZuulRoute> routeBuilder = Stream.builder();

        if (clusterConfig.getPrometheus() != null && clusterConfig.getPrometheus().isProxy()) {
//            String url = "/" + key + "/proxy/prometheus" + DiscoveryClientRouteLocator.DEFAULT_ROUTE; // cluster 대응시 변경
            String url = "/proxy/prometheus" + DiscoveryClientRouteLocator.DEFAULT_ROUTE;
            ZuulProperties.ZuulRoute route = getZuulRoute(key + "-prometheus", url, clusterConfig.getPrometheus().getUrl());
            routeBuilder.add(route);
        }

        if (clusterConfig.getLoki() != null && clusterConfig.getLoki().isProxy()) {
//            String url = "/" + key + "/proxy/loki" + DiscoveryClientRouteLocator.DEFAULT_ROUTE; // cluster 대응시 변경
            String url = "/proxy/loki" + DiscoveryClientRouteLocator.DEFAULT_ROUTE;
            ZuulProperties.ZuulRoute route = getZuulRoute(key + "-loki", url, clusterConfig.getLoki().getUrl());
            routeBuilder.add(route);
        }

        return routeBuilder.build();
    }

    private ZuulProperties.ZuulRoute getZuulRoute(String key, String path, String url) {
        ZuulProperties.ZuulRoute route = new ZuulProperties.ZuulRoute();
        route.setId(key);
        route.setPath(path);
        route.setUrl(url);
        route.setStripPrefix(true);
        return route;
    }

    @Slf4j
    public static class DefaultRouteLocator extends SimpleRouteLocator {

        private final ZuulProperties properties;
        private final AtomicReference<Map<String, ZuulProperties.ZuulRoute>> routes = new AtomicReference<>();
        private final String dispatcherServletPath;
        private final String zuulServletPath;
        private final PathMatcher pathMatcher = new AntPathMatcher();

        public DefaultRouteLocator(final String servletPath, final ZuulProperties properties, Map<String, ZuulProperties.ZuulRoute> routes) {
            super(servletPath, properties);
            this.properties = properties;
            this.routes.set(routes);
            if (StringUtils.hasText(servletPath)) {
                this.dispatcherServletPath = servletPath;
            } else {
                this.dispatcherServletPath = "/";
            }
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
                            log.warn("Invalid route, routeId: {}, routeServiceId: {}, msg: {}",
                                zuulRoute.getId(), zuulRoute.getServiceId(), e.getMessage());
                        }
                        if (log.isDebugEnabled()) { log.debug("", e); }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }

        /**
         * copy in SimpleRouteLocator{@link SimpleRouteLocator#getMatchingRoute}
         */
        @Override
        public Route getMatchingRoute(String path) {
            String adjustedPath = this.getAdjustPath(path);
            ZuulProperties.ZuulRoute route = this.getZuulRoute(adjustedPath);
            return super.getRoute(route, adjustedPath);
        }

        /**
         * copy in SimpleRouteLocator{@link SimpleRouteLocator#adjustPath}
         */
        protected String getAdjustPath(final String path) {
            String adjustedPath = path;

            if (RequestUtils.isDispatcherServletRequest()
                && StringUtils.hasText(this.dispatcherServletPath)) {
                if (!this.dispatcherServletPath.equals("/")
                    && path.startsWith(this.dispatcherServletPath)) {
                    adjustedPath = path.substring(this.dispatcherServletPath.length());
                    log.debug("Stripped dispatcherServletPath");
                }
            }
            else if (RequestUtils.isZuulServletRequest()) {
                if (StringUtils.hasText(this.zuulServletPath)
                    && !this.zuulServletPath.equals("/")) {
                    adjustedPath = path.substring(this.zuulServletPath.length());
                    log.debug("Stripped zuulServletPath");
                }
            }
            else {
                log.warn("do nothing");
            }

            log.debug("adjustedPath=" + adjustedPath);
            return adjustedPath;
        }

        @Override
        protected ZuulProperties.ZuulRoute getZuulRoute(String adjustedPath) {
            if (matchesIgnoredPatterns(adjustedPath)) {
                return null;
            }
            return this.routes.get().entrySet().stream()
                .filter(entry -> this.pathMatcher.match(entry.getKey(), adjustedPath))
                .findFirst().map(Map.Entry::getValue).orElse(null);
        }

        @Override
        protected void doRefresh() {
            log.warn("no nothing");
        }

        public void refreshRoutes(final Map<String, ZuulProperties.ZuulRoute> newRoutes) {
            this.routes.set(newRoutes);
            log.info("refresh zuul routes");
            if (log.isDebugEnabled()) {
                log.debug("newRoutes={}", newRoutes);
            }
            doRefresh();
        }
    }

}

