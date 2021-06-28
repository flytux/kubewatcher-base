package com.kubeworks.watcher.config;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.*;
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
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
public class SecurityNativeConfig {

    private static final String DENIED_PAGE = "error/403";
    private static final String PROTECTED_PAGE = "protected-page";

    private static final String UNSUPPORTED_MESSAGE = "Cannot instantiate this class";

    private static final String USER_ID_PARAM = "preAuthUser";
    private static final String SESSION_ID_PARAM = "preAuthSession";
    private static final Pattern SESSION_ID_PATTERN = Pattern.compile("^[A-Za-z0-9/+=]{28}$", Pattern.UNICODE_CHARACTER_CLASS);

    private static final String[] NO_CSRF_PATTERNS = {"/sso/**", "/h2-console/**"};
    private static final String[] NO_AUTH_PATTERNS = {"/login*", "/logout*", "/error*", "/sso/**", "/h2-console/**"};
    private static final String[] STATIC_RESOURCES_PATTERNS = {"/assets/**", "/vendor/**", "/**/*.js.map", "/favicon.ico"};

    private static RequestMatcher createRequestMatcher () {
        return new AntPathRequestMatcher("/login", "POST");
    }

    @Getter @Setter
    @ConfigurationProperties(prefix="application.properties.securities")
    static class SecProperties {
        private String path = "";
        private String fakePass = "";
    }

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
                log.warn("Menu or Rule information incorrect !!"); return ImmutableSet.of();
            }

            final List<String> source = ImmutableList.copyOf(Splitter.fixedLength(1).split(rules));
            final Set<Long> menuIds = Sets.newHashSetWithExpectedSize((int)source.stream().filter("1"::equals).count());

            IntStream.range(0, menus.size()).forEachOrdered(m -> {
                if ("1".equals(source.get(m))) { menuIds.add(menus.get(m).getMenuId()); }
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

        public static boolean checkAuthorities(final Long menuId) {

            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.isNull(auth)) { return false; }

            final Object o = auth.getDetails();
            if (Objects.isNull(o) || !(o instanceof ExtendedWebAuthenticationDetails)) { return false; }

            return ((ExtendedWebAuthenticationDetails)o).getMenuAuthorities().contains(menuId);
        }
    }

    static final class CRLFConverter {

        private static final Pattern CRLF_PATTERN = Pattern.compile("[\r\n]");

        private CRLFConverter() {
            throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
        }

        static String sanitize(final String source) {
            return CRLF_PATTERN.matcher(source).replaceAll("_CRLF_");
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
    @EnableConfigurationProperties(value=SecProperties.class)
    protected static class SecConfig extends WebSecurityConfigurerAdapter {

        private final SecProperties props;
        private final PasswordEncoder encoder;
        private final UserDetailsService service;
        private final MenuAuthoritiesHandler handler;

        @Autowired
        protected SecConfig(final SecProperties props, final PasswordEncoder encoder,
                            final UserDetailsService service, final MenuAuthoritiesHandler handler) {

            this.props = props;
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
            http.logout().logoutSuccessHandler(new ExtendedLogoutSuccessHandler());
            http.addFilterBefore(new PreAuthenticatedCsrfSkipFilter(), CsrfFilter.class);
            http.addFilter(new ExtendedPreAuthenticatedProcessFilter(authenticationManager()));
            http.addFilter(new ExtendedUsernamePasswordAuthenticationFilter(authenticationManager(), handler));
            http.authorizeRequests().antMatchers(NO_AUTH_PATTERNS).permitAll().anyRequest().authenticated().accessDecisionManager(new UnanimousBased(voters));
        }

        @Override
        protected void configure(final AuthenticationManagerBuilder builder) {

            final UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> wrapper =
                new UserDetailsByNameServiceWrapper<>(new FakeUserDetailsService(props));
            final PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(wrapper);

            builder.authenticationProvider(new ExtendedDaoAuthenticationProvider(encoder, service));
            builder.authenticationProvider(provider);
        }

        @Override
        public void configure(final WebSecurity web) throws Exception {
            web.ignoring().antMatchers(STATIC_RESOURCES_PATTERNS);
        }
    }

    @Controller
    @EnableConfigurationProperties(value=SecProperties.class)
    public static class LoginLogoutController {

        private final SecProperties props;

        @Autowired
        public LoginLogoutController(final SecProperties props) {
            this.props = props;
        }

        @GetMapping("/login")
        public String viewLoginPage() {
            return unauthenticated() ? "login" : "redirect:/main";
        }

        @GetMapping(value="/sso/protected-page")
        public String viewProtectedPage(@RequestHeader final HttpHeaders headers, final Model model) {

            if (Objects.isNull(headers.getFirst(HttpHeaders.REFERER))) { return DENIED_PAGE; }

            model.addAttribute("path", props.getPath());
            model.addAttribute(USER_ID_PARAM, headers.getFirst("SM_USER"));
            model.addAttribute(SESSION_ID_PARAM, headers.getFirst("SM_SERVERSESSIONID"));

            return PROTECTED_PAGE;
        }

        @GetMapping("/sso/logout")
        public String viewSSOLogoutPage(@RequestHeader final HttpHeaders headers) {
            return Objects.nonNull(headers.getFirst(HttpHeaders.REFERER)) && unauthenticated() ? "logout" : DENIED_PAGE;
        }

        private boolean unauthenticated() {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return Objects.isNull(auth) || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated();
        }
    }

    @Controller @Profile("mac")
    @EnableConfigurationProperties(value=SecProperties.class)
    public static class SSOTestController {

        private final SecProperties props;

        @Autowired
        public SSOTestController(final SecProperties props) {
            this.props = props;
        }

        @PostMapping("/sso/mock-sso-login")
        public String mock(@RequestParam("USER") final String user, final Model model) {
            putAttributeTo(model, user); return PROTECTED_PAGE;
        }

        @GetMapping(value="/sso/mock-protected-page")
        public String viewProtectedPage(@RequestHeader final HttpHeaders headers, final Model model) {

            if (Objects.nonNull(headers.getFirst(HttpHeaders.REFERER))) {
                putAttributeTo(model, "test"); return PROTECTED_PAGE;
            }

            return DENIED_PAGE;
        }

        private void putAttributeTo(final Model model, final String user) {
            model.addAttribute("mode", "mock").addAttribute("path", props.getPath());
            model.addAttribute(USER_ID_PARAM, user).addAttribute(SESSION_ID_PARAM, "NxWjs20P/pMmfqyG5jjY4urwUyc=");
        }
    }

    @Slf4j
    static class MenuAuthoritiesVoter implements AccessDecisionVoter<FilterInvocation> {

        private static final String ROOT_PATH = "/";
        private static final List<String> PROXY_PATHS = ImmutableList.of("/proxy/loki/", "/proxy/prometheus/", "/proxy/prometheusTBD");
        private static final UrlPathHelper HELPER = new UrlPathHelper();

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

            if (ROOT_PATH.equals(requested) || PROXY_PATHS.stream().anyMatch(requested::startsWith)) {
                log.debug("Request matched to either '/' or zuul proxy managed path -> {}", requested);
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

    static final class PreAuthenticatedChecker {

        static boolean preAuthenticated() {
            final Authentication pre = SecurityContextHolder.getContext().getAuthentication();
            return Objects.nonNull(pre) && pre instanceof PreAuthenticatedAuthenticationToken;
        }

        @SuppressWarnings("unchecked")
        static String retrieveUserFrom(final HttpServletRequest request) {
            return Optional.ofNullable((Map<String, String>)request.getAttribute(USER_ID_PARAM))
                .map(e -> e.get(USER_ID_PARAM)).filter(StringUtils::hasText).map(String::trim).orElse(null);
        }

        private PreAuthenticatedChecker() {
            throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
        }
    }

    static class FakeUserDetailsService implements UserDetailsService {

        private final SecProperties props;

        FakeUserDetailsService(final SecProperties props) {
            this.props = props;
        }

        @Override
        public UserDetails loadUserByUsername(final String username) {
            return User.withUsername(username).password(props.getFakePass()).authorities("ROLE_TEMP").build();
        }
    }

    @Slf4j
    static class ExtendedDaoAuthenticationProvider extends DaoAuthenticationProvider implements Ordered {

        ExtendedDaoAuthenticationProvider(final PasswordEncoder encoder, final UserDetailsService service) {

            super();
            super.setPasswordEncoder(encoder);
            super.setUserDetailsService(service);
        }

        @Override
        protected void additionalAuthenticationChecks(
            final UserDetails user, final UsernamePasswordAuthenticationToken token) {

            if (Objects.isNull(token.getCredentials())) {
                createException(() -> log.debug("Authentication failed: no credentials provided"));
            }

            if (PreAuthenticatedChecker.preAuthenticated()) {
                log.info("PreAuthenticated user '{}' skip password equality check", user.getUsername()); return;
            }

            if (!getPasswordEncoder().matches(token.getCredentials().toString(), user.getPassword())) {
                createException(() -> log.debug("Authentication failed: password does not match stored value"));
            }
        }

        @Override public int getOrder() { return 0; }

        private void createException(final Runnable fn) {
            fn.run();
            throw new BadCredentialsException(
                messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    @Slf4j
    static class ExtendedLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

        @Override
        public void onLogoutSuccess(final HttpServletRequest request,
                                    final HttpServletResponse response, final Authentication auth) throws IOException {

            final String path = resolveLogoutPath(auth);
            if (response.isCommitted()) {
                log.debug("Response has already been committed. Unable to redirect to {}", path); return;
            }

            getRedirectStrategy().sendRedirect(request, response, path);
        }

        private String resolveLogoutPath(final Authentication auth) {

            final Object details = auth.getDetails();
            if (ExtendedWebAuthenticationDetails.class.isAssignableFrom(details.getClass())) {
                return ((ExtendedWebAuthenticationDetails)details).isPreAuthUser() ? "/sso/logout" : "/login?logout";
            }

            return "/login?logout";
        }
    }

    @Slf4j
    static class PreAuthenticatedCsrfSkipFilter extends OncePerRequestFilter {

        private final RequestMatcher matcher;

        PreAuthenticatedCsrfSkipFilter() {
            this.matcher = createRequestMatcher();
        }

        @Override
        protected void doFilterInternal(@NonNull final HttpServletRequest req, @NonNull final HttpServletResponse res,
                                        @NonNull final FilterChain chain) throws ServletException, IOException {

            final String session = req.getParameter(SESSION_ID_PARAM);

            if (matcher.matches(req) && StringUtils.hasText(session) && SESSION_ID_PATTERN.matcher(session).matches()) {
                log.info("PreAuthenticated Login with session {} -> skip CSRFFILTER!", CRLFConverter.sanitize(session));
                CsrfFilter.skipRequest(req);
            }

            chain.doFilter(req, res);
        }
    }

    static class PreAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

        @Override
        public void onAuthenticationSuccess(
            final HttpServletRequest request, final HttpServletResponse response, final Authentication auth) {
            request.setAttribute(USER_ID_PARAM, createPreAuthenticatedInformation(request));
        }

        private static Map<String, String> createPreAuthenticatedInformation(final HttpServletRequest req) {
            return ImmutableMap.of(USER_ID_PARAM,
                req.getParameter(USER_ID_PARAM).trim(), SESSION_ID_PARAM, req.getParameter(SESSION_ID_PARAM).trim());
        }
    }

    static class PreAuthenticationFailureHandler implements AuthenticationFailureHandler {

        @Override
        public void onAuthenticationFailure(final HttpServletRequest request,
                                            final HttpServletResponse response, final AuthenticationException e) throws IOException {
            response.sendRedirect("/login?smerror");
        }
    }

    @Slf4j
    static class ExtendedPreAuthenticatedProcessFilter extends AbstractPreAuthenticatedProcessingFilter {

        private final RequestMatcher matcher;
        private final AuthenticationManager manager;

        ExtendedPreAuthenticatedProcessFilter(final AuthenticationManager manager) {

            super(); super.setAuthenticationManager(manager);
            super.setAuthenticationFailureHandler(new PreAuthenticationFailureHandler());
            super.setAuthenticationSuccessHandler(new PreAuthenticationSuccessHandler());
            this.matcher = createRequestMatcher(); this.manager = manager;
        }

        @Override
        protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {

            final String user = request.getParameter(USER_ID_PARAM);
            final String session = request.getParameter(SESSION_ID_PARAM);

            if (StringUtils.hasText(session)) {
                if (!SESSION_ID_PATTERN.matcher(session).matches()) {
                    throw new PreAuthenticatedCredentialsNotFoundException("PreAuthenticated sessionId invalid");
                }

                final Enumeration<String> referer = request.getHeaders(HttpHeaders.REFERER);
                if (Objects.isNull(referer) || !referer.hasMoreElements()) {
                    throw new InsufficientAuthenticationException("Unreliable request : referer not found");
                }

                if (!StringUtils.hasText(user)) {
                    throw new PreAuthenticatedCredentialsNotFoundException(USER_ID_PARAM + " parameter invalid");
                }
            }

            return StringUtils.hasLength(user) ? user.trim() : user;
        }

        @Override
        protected Object getPreAuthenticatedCredentials(final HttpServletRequest request) {
            return "N/A";
        }

        @Override
        public void doFilter(final ServletRequest req,
                             final ServletResponse res, final FilterChain chain) throws IOException, ServletException {

            final HttpServletRequest request = (HttpServletRequest)req;
            final HttpServletResponse response = (HttpServletResponse)res;

            log.debug("Checking secure context token: {}", SecurityContextHolder.getContext().getAuthentication());

            if (!matches(request)) { chain.doFilter(request, response); return; }

            Object user;
            try {
                user = getPreAuthenticatedPrincipal(request);
            } catch (final PreAuthenticatedCredentialsNotFoundException e) {
                unsuccessfulAuthentication(request, response, e); return;
            }

            if (Objects.isNull(user)) {
                log.debug("No pre-authenticated principal found in request");
                chain.doFilter(request, response); return;
            }

            log.debug("preAuthenticatedPrincipal = {}, trying to authenticate", CRLFConverter.sanitize((String)user));

            try {
                final PreAuthenticatedAuthenticationToken token =
                    new PreAuthenticatedAuthenticationToken(user, getPreAuthenticatedCredentials(request));
                token.setDetails(getAuthenticationDetailsSource().buildDetails(request));

                successfulAuthentication(request, response, manager.authenticate(token));
            } catch (final AuthenticationException e) {
                unsuccessfulAuthentication(request, response, e); return;
            }

            chain.doFilter(request, response);
        }

        private boolean matches(final HttpServletRequest request) {

            if (!matcher.matches(request)) { return false; }

            final Authentication current = SecurityContextHolder.getContext().getAuthentication();

            if (Objects.isNull(current)) { return true; }
            if (!principalChanged(request, current)) { return false; }

            log.debug("Pre-authenticated principal has changed and will be re-authenticated");

            SecurityContextHolder.clearContext();

            final HttpSession session = request.getSession(false);

            if (Objects.nonNull(session)) {
                log.debug("Invalidating existing session"); session.invalidate(); request.getSession();
            }

            return true;
        }
    }

    @Getter @ToString
    static class ExtendedWebAuthenticationDetails extends WebAuthenticationDetails {

        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        private final boolean preAuthUser;
        private final Set<Long> menuAuthorities;
        private final Map<String, Long> pathAuthorities;

        ExtendedWebAuthenticationDetails(final HttpServletRequest request, final boolean preAuthUser) {
            this(request, preAuthUser, ImmutableSet.of(), ImmutableMap.of());
        }

        ExtendedWebAuthenticationDetails(final HttpServletRequest request,
                                         final boolean preAuthUser, final Set<Long> menuAuthorities, final Map<String, Long> pathAuthorities) {

            super(request);
            this.preAuthUser = preAuthUser;
            this.menuAuthorities = ImmutableSet.copyOf(menuAuthorities);
            this.pathAuthorities = ImmutableMap.copyOf(pathAuthorities);
        }

        @Override
        public boolean equals(final Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    static class ExtendedAuthenticationDetailsSource implements
        AuthenticationDetailsSource<HttpServletRequest, ExtendedWebAuthenticationDetails> {

        @Override
        public ExtendedWebAuthenticationDetails buildDetails(final HttpServletRequest context) {
            return new ExtendedWebAuthenticationDetails(context,
                PreAuthenticatedChecker.preAuthenticated() && Objects.nonNull(context.getAttribute(USER_ID_PARAM)));
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
                ((UsernamePasswordAuthenticationToken)auth).setDetails(new ExtendedWebAuthenticationDetails(
                    request, ((ExtendedWebAuthenticationDetails)o).isPreAuthUser(), menuIds, handler.retrieveMenuMappings(menuIds)));

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
            getRedirectStrategy().sendRedirect(request, response, "/login?" + decideFailureParam(request, e));
        }

        private static String decideFailureParam(final HttpServletRequest request, final AuthenticationException e) {

            if (Objects.nonNull(request.getAttribute(USER_ID_PARAM))) {
                return checkLoginFailureException(e) ? "unknown" : "weird";
            }

            return "error";
        }

        private static boolean checkLoginFailureException(final AuthenticationException e) {
            return e instanceof UsernameNotFoundException || e instanceof BadCredentialsException;
        }
    }

    static class ExtendedUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

        ExtendedUsernamePasswordAuthenticationFilter(
            final AuthenticationManager manager, final MenuAuthoritiesHandler handler) {

            super(); super.setAuthenticationManager(manager);
            super.setAuthenticationDetailsSource( new ExtendedAuthenticationDetailsSource ());
            super.setAuthenticationSuccessHandler(new ExtendedAuthenticationSuccessHandler(handler));
            super.setAuthenticationFailureHandler(new ExtendedAuthenticationFailureHandler());
        }

        @Override
        protected String obtainUsername(final HttpServletRequest request) {
            return PreAuthenticatedChecker.preAuthenticated() ?
                PreAuthenticatedChecker.retrieveUserFrom(request) : super.obtainUsername(request);
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
