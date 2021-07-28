package com.kubeworks.watcher.config;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
public class SecurityNativeConfig {

    private static final String UNSUPPORTED_MESSAGE = "Cannot instantiate this class";

    private static final String[] NO_CSRF_PATTERNS = {"/h2-console/**"};
    private static final String[] NO_AUTH_PATTERNS = {"/login*", "/logout*", "/error*", "/h2-console/**"};
    private static final String[] STATIC_RESOURCES_PATTERNS = {"/assets/**", "/vendor/**", "/**/*.css.map", "/**/*.js.map", "/**/*.map", "/favicon.ico"};

    @Getter @ToString
    public static class MenuInfo {

        private final Long pageId;
        private final Long menuId;

        MenuInfo(final Long pageId, final Long menuId) {
            this.pageId = pageId;
            this.menuId = menuId;
        }
    }

    @Mapper
    interface MenuAuthoritiesMapper {

        @Select("SELECT P.PAGE_ID, P.MENU_ID FROM page P ORDER BY P.PAGE_ID ASC")
        List<MenuInfo> searchPageInfos();

        @Select("SELECT KURR.RULE FROM kw_user_role_rule KURR WHERE KURR.RULE_ID = " +
            "(SELECT KUR.RULE_ID FROM kw_user_role KUR WHERE KUR.USERNAME = #{user})")
        String searchUserRule(final String user);
    }

    interface MenuAuthoritiesHandler {
        Set<Long> retrieveMenuIds(final String user);
        Map<String, Long> retrieveMenuMappings(final Set<Long> source);
    }

    @Slf4j
    static class MenuAuthoritiesHandlerImpl implements MenuAuthoritiesHandler {

        private static final Integer SIZE = 2;
        private static final String HAS_AUTH_VALUE = "1";
        private static final String PATH = "classpath:menu.txt";

        private final MenuAuthoritiesMapper mapper;

        MenuAuthoritiesHandlerImpl(final MenuAuthoritiesMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public Set<Long> retrieveMenuIds(final String user) {

            final List<MenuInfo> menus = mapper.searchPageInfos();
            final String rules = Strings.nullToEmpty(mapper.searchUserRule(user)).trim();

            if (CollectionUtils.isEmpty(menus) || !StringUtils.hasText(rules) || menus.size() != rules.length()) {
                log.warn("Menu or Rule information incorrect"); return ImmutableSet.of();
            }

            final List<String> source = ImmutableList.copyOf(Splitter.fixedLength(1).split(rules));
            final Set<Long> menuIds = Sets.newHashSetWithExpectedSize((int)source.stream().filter(HAS_AUTH_VALUE::equals).count());

            IntStream.range(0, menus.size()).forEachOrdered(m -> {
                if (HAS_AUTH_VALUE.equals(source.get(m))) { menuIds.add(menus.get(m).getMenuId()); }
            });

            return menuIds;
        }

        @Override
        public Map<String, Long> retrieveMenuMappings(final Set<Long> source) {
            return retrieveMenuPathInformation().entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getValue, e -> source.contains(e.getKey()) ? e.getKey() : Long.MIN_VALUE));
        }

        private Map<Long, String> retrieveMenuPathInformation() {

            final Map<Long, String> res = Maps.newHashMap();

            try {
                Files.readAllLines(ResourceUtils.getFile(PATH).toPath(), StandardCharsets.UTF_8).forEach(e ->
                    makeIterable(e, ',').forEach(m -> Optional.of(ImmutableList.copyOf(makeIterable(m, '|')))
                        .filter(kv -> SIZE == kv.size()).ifPresent(kv -> res.put(Long.valueOf(kv.get(0)), kv.get(1)))));
            } catch (final IOException e) {
                log.warn("Error while reading resource {}", PATH); log.warn("", e);
            }

            return res;
        }

        private static Iterable<String> makeIterable(final String source, final char s) {
            return Splitter.on(s).omitEmptyStrings().trimResults().split(source);
        }
    }

    public static final class MenuAuthoritiesChecker {

        private MenuAuthoritiesChecker() {
            throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
        }

        public static Set<Long> retrieveAuthorities() {

            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.isNull(auth)) { return ImmutableSet.of(); }

            final Object o = auth.getDetails();
            if (Objects.isNull(o) || !(o instanceof ExtendedWebAuthenticationDetails)) { return ImmutableSet.of(); }

            return ((ExtendedWebAuthenticationDetails)o).getMenuAuthorities();
        }

        public static boolean containsAnyMenuId(final Set<Long> menuAuthorities, final Long... menuIds) {
            return Arrays.stream(menuIds).anyMatch(menuAuthorities::contains);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MenuAuthoritiesHandler menuAuthoritiesHandler(final MenuAuthoritiesMapper mapper) {
        return new MenuAuthoritiesHandlerImpl(mapper);
    }

    @Configuration
    @EnableWebSecurity
    protected static class SecConfig extends WebSecurityConfigurerAdapter {

        private final PasswordEncoder encoder;
        private final UserDetailsService service;
        private final MenuAuthoritiesHandler handler;

        @Autowired
        protected SecConfig(final PasswordEncoder encoder, final UserDetailsService service, final MenuAuthoritiesHandler handler) {

            this.encoder = encoder;
            this.service = service;
            this.handler = handler;
        }

        @Override
        protected void configure(final HttpSecurity http) throws Exception {

            final List<AccessDecisionVoter<?>> voters = ImmutableList.of(
                new WebExpressionVoter(), new RoleVoter(), new AuthenticatedVoter(), new MenuAuthoritiesVoter());

            http.httpBasic().and().exceptionHandling();
            http.csrf().ignoringAntMatchers(NO_CSRF_PATTERNS);
            http.headers().frameOptions().sameOrigin().xssProtection();
            http.formLogin().loginPage("/login").defaultSuccessUrl("/main");
            http.addFilter(new ExtendedUsernamePasswordAuthenticationFilter(authenticationManager(), handler));
            http.authorizeRequests().antMatchers(NO_AUTH_PATTERNS).permitAll().anyRequest().authenticated().accessDecisionManager(new UnanimousBased(voters));
        }

        @Override
        protected void configure(final AuthenticationManagerBuilder builder) {
            builder.authenticationProvider(new ExtendedDaoAuthenticationProvider(encoder, service));
        }

        @Override
        public void configure(final WebSecurity web) throws Exception {
            web.ignoring().antMatchers(STATIC_RESOURCES_PATTERNS);
        }
    }

    @Controller
    public static class LoginLogoutController {

        @GetMapping("/login")
        public String viewLoginPage() {
            return unauthenticated() ? "login" : "redirect:/main";
        }

        private boolean unauthenticated() {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return Objects.isNull(auth) || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated();
        }
    }

    @Slf4j
    static class MenuAuthoritiesVoter implements AccessDecisionVoter<FilterInvocation> {

        private static final UrlPathHelper HELPER = new UrlPathHelper();

        private static final String ROOT_PATH = "/";
        private static final List<String> EXCLUSION_PATH = ImmutableList.of("/api/v1/", "/proxy/loki/", "/proxy/prometheus/", "/proxy/prometheusTBD");

        @Override
        public boolean supports(final ConfigAttribute attribute) { return true; }

        @Override
        public boolean supports(final Class<?> clazz) {
            return clazz.isAssignableFrom(FilterInvocation.class);
        }

        @Override
        public int vote(final Authentication auth, final FilterInvocation f, final Collection<ConfigAttribute> c) {

            final String requested = HELPER.getOriginatingRequestUri(f.getHttpRequest());

            if (c.stream().anyMatch(e -> "permitAll".equals(e.toString()))) {
                log.debug("Request matched to NO_AUTH_PATTERNS -> {}", requested);
                return ACCESS_GRANTED;
            }

            if (AnonymousAuthenticationToken.class.isAssignableFrom(auth.getClass())) {
                log.debug("Anonymous user do not have ExtendedWebAuthenticationDetails -> abstain voting");
                return ACCESS_ABSTAIN;
            }

            final Object details = auth.getDetails();

            if (Objects.isNull(details) || !(details instanceof ExtendedWebAuthenticationDetails)
                    || CollectionUtils.isEmpty(((ExtendedWebAuthenticationDetails)details).getPathAuthorities())) {
                log.warn("cannot retrieve MenuPath object -> user : {}", auth.getName());
                return ACCESS_ABSTAIN;
            }

            if (ROOT_PATH.equals(requested) || EXCLUSION_PATH.stream().anyMatch(requested::startsWith)) {
                log.debug("Request matched to either '/' or zuul proxy managed path or rest api path -> {}", requested);
                return ACCESS_GRANTED;
            }

            // 우선 메뉴에 걸려 있는 URL에 대해서만 권한 처리

            return ((ExtendedWebAuthenticationDetails)details).getPathAuthorities().entrySet().stream()
                .filter(e -> requested.startsWith(e.getKey())).map(Map.Entry::getValue).findFirst().map(m -> {
                    if (Long.MIN_VALUE != m) { return ACCESS_GRANTED; }
                    log.info("User {} has no authorisation for {} !", auth.getName(), requested); return ACCESS_DENIED;
                }).orElseGet(() -> {
                    log.info("Path {} not one of menu -> Abstain decision", requested); return ACCESS_ABSTAIN;
                });
        }
    }

    @Slf4j
    static class ExtendedDaoAuthenticationProvider extends DaoAuthenticationProvider implements Ordered {

        ExtendedDaoAuthenticationProvider(final PasswordEncoder encoder, final UserDetailsService service) {

            super();
            super.setPasswordEncoder(encoder);
            super.setUserDetailsService(service);
        }

        @Override public int getOrder() { return 0; }
    }

    @Getter @ToString @EqualsAndHashCode(callSuper=true)
    static class ExtendedWebAuthenticationDetails extends WebAuthenticationDetails {

        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        private final Set<Long> menuAuthorities;
        private final Map<String, Long> pathAuthorities;

        ExtendedWebAuthenticationDetails(final HttpServletRequest request) {
            this(request, ImmutableSet.of(), ImmutableMap.of());
        }

        ExtendedWebAuthenticationDetails(final HttpServletRequest request, final Set<Long> menuAuthorities, final Map<String, Long> pathAuthorities) {

            super(request);
            this.menuAuthorities = ImmutableSet.copyOf(menuAuthorities);
            this.pathAuthorities = ImmutableMap.copyOf(pathAuthorities);
        }
    }

    static class ExtendedAuthenticationDetailsSource implements
            AuthenticationDetailsSource<HttpServletRequest, ExtendedWebAuthenticationDetails> {

        @Override
        public ExtendedWebAuthenticationDetails buildDetails(final HttpServletRequest context) {
            return new ExtendedWebAuthenticationDetails(context);
        }
    }

    static class ExtendedAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

        private final MenuAuthoritiesHandler handler;

        ExtendedAuthenticationSuccessHandler(final MenuAuthoritiesHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onAuthenticationSuccess(final HttpServletRequest request,
                final HttpServletResponse response, final Authentication auth) throws ServletException, IOException {

            final Object o = auth.getDetails();
            if (o instanceof ExtendedWebAuthenticationDetails) {
                final Set<Long> menuIds = handler.retrieveMenuIds(auth.getName());
                ((UsernamePasswordAuthenticationToken)auth).setDetails(new ExtendedWebAuthenticationDetails(request, menuIds, handler.retrieveMenuMappings(menuIds)));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            super.onAuthenticationSuccess(request, response, auth);
        }
    }

    static class ExtendedAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(final HttpServletRequest request,
                final HttpServletResponse response, final AuthenticationException e) throws IOException {

            super.saveException(request, e);
            getRedirectStrategy().sendRedirect(request, response, "/login?error");
        }
    }

    static class ExtendedUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

        ExtendedUsernamePasswordAuthenticationFilter(
                final AuthenticationManager manager, final MenuAuthoritiesHandler handler) {

            super(); super.setAuthenticationManager(manager);
            super.setAuthenticationDetailsSource (new ExtendedAuthenticationDetailsSource ());
            super.setAuthenticationFailureHandler(new ExtendedAuthenticationFailureHandler());
            super.setAuthenticationSuccessHandler(new ExtendedAuthenticationSuccessHandler(handler));
        }
    }

    @Bean
    public BeanPostProcessor springSecurityFilterChainPostProcessor() {
        return new SpringSecurityFilterChainPostProcessor();
    }

    static class SpringSecurityFilterChainPostProcessor implements BeanPostProcessor {

        @Override
        public Object postProcessAfterInitialization(
                @NonNull final Object bean, @NonNull final String name) throws BeansException {

            if (FilterChainProxy.class.isAssignableFrom(bean.getClass())) {

                final List<SecurityFilterChain> source = ((FilterChainProxy)bean).getFilterChains();
                final List<SecurityFilterChain> chains = Lists.newArrayListWithExpectedSize(source.size());

                source.forEach(s -> chains.add(createSecurityFilterChain(s)));

                final FilterChainProxy p = new FilterChainProxy(chains); p.afterPropertiesSet();

                return p;
            }

            return BeanPostProcessor.super.postProcessAfterInitialization(bean, name);
        }

        private static SecurityFilterChain createSecurityFilterChain(final SecurityFilterChain chain) {

            final Predicate<Filter> predicate = o -> o instanceof ExtendedUsernamePasswordAuthenticationFilter ||
                !(o instanceof UsernamePasswordAuthenticationFilter);
            final List<Filter> filtered = chain.getFilters().stream().filter(predicate).collect(Collectors.toList());

            return new DefaultSecurityFilterChain(((DefaultSecurityFilterChain)chain).getRequestMatcher(), filtered);
        }
    }
}
