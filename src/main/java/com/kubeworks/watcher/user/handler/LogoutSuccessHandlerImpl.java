package com.kubeworks.watcher.user.handler;

import com.kubeworks.watcher.config.properties.GrafanaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Value("${application.properties.root-domain}")
    private String rootDomain;

    private final GrafanaProperties grafanaProperties;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {

        Optional<Cookie> grafanaCookie = Arrays.stream(request.getCookies())
            .filter(cookie -> StringUtils.equalsIgnoreCase(cookie.getName(), grafanaProperties.getLoginCookieName()))
            .findFirst();

        if (grafanaCookie.isPresent()) {
            Cookie cookie = grafanaCookie.get();
            log.info("cookie = {}", cookie);
            cookie.setMaxAge(0);
            cookie.setDomain(rootDomain);
            response.addCookie(cookie);
        }

        response.sendRedirect("/login");

    }
}
