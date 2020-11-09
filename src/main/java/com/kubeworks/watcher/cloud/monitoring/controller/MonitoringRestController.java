package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.config.properties.PrometheusProperties;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping(path = "/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class MonitoringRestController {

    private static final long DATABASE_MENU_ID = 130;
    private static final long JVM_OVERVIEW_MENU_ID = 120;
    private static final long JVM_DETAIL_MENU_ID = 121;
    private static final long VM_OVERVIEW_MENU_ID = 140;
    private static final long VM_DETAIL_MENU_ID = 141;

    private final PageViewService pageViewService;
    private final PrometheusProperties prometheusProperties;

    @GetMapping(value = "/monitoring/database", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> database() {
        Page pageView = pageViewService.getPageView(DATABASE_MENU_ID);
        return responseData(pageView);
    }

    @GetMapping(value = "/monitoring/jvm/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> jvmOverview() {
        Page pageView = pageViewService.getPageView(JVM_OVERVIEW_MENU_ID);
        return responseData(pageView);
    }

    @GetMapping(value = "/monitoring/jvm/application", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> jvmDetail() {
        Page pageView = pageViewService.getPageView(JVM_DETAIL_MENU_ID);
        return responseData(pageView);
    }

    @GetMapping(value = "/monitoring/vm/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> vmOverview() {
        Page pageView = pageViewService.getPageView(VM_OVERVIEW_MENU_ID);
        return responseData(pageView);
    }

    @GetMapping(value = "/monitoring/vm/monitoring", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> vmDetail() {
        Page pageView = pageViewService.getPageView(VM_DETAIL_MENU_ID);
        return responseData(pageView);
    }

    private Map<String, Object> responseData(Page page) {
        Map<String, Object> response = new HashMap<>();
        response.put("user", getUser());
        response.put("host", prometheusProperties.getUrl());
        response.put("page", page);
        return response;
    }

    protected User getUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }


}
