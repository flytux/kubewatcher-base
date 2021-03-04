package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.cloud.monitoring.service.PageMetricService;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
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
import java.util.stream.Collectors;

@RestController()
@RequestMapping(path = "/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class MonitoringRestController {

    private static final long APPLICATION_OVERVIEW_MENU_ID = 100;
    private static final long CLUSTER_OVERVIEW_MENU_ID = 111;
    private static final long CLUSTER_WORKLOADS_OVERVIEW_MENU_ID = 1120;
    private static final long DATABASE_MENU_ID = 130;
    private static final long JVM_OVERVIEW_MENU_ID = 120;
    private static final long JVM_DETAIL_MENU_ID = 121;
    private static final long VM_OVERVIEW_MENU_ID = 140;
    private static final long VM_DETAIL_MENU_ID = 141;
    private static final long MAIN_MENU_ID = 99;
    private static final long LOGGING_MENU_ID = 1128;

    private final PageViewService pageViewService;
    private final MonitoringProperties monitoringProperties;
    private final PageMetricService<Page> applicationPageMetricService;
    private final ApplicationService applicationService;

    @GetMapping(value = "/monitoring/logging", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> logging() {
        Page pageView = pageViewService.getPageView(LOGGING_MENU_ID);
        return lokiresponseData(pageView);
    }

    @GetMapping(value = "/monitoring/application/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> application() {
        Map<String, Object> response = responseData(applicationPageMetricService.pageMetrics(APPLICATION_OVERVIEW_MENU_ID));
        response.put("services", applicationService);
        return response;
    }

    @GetMapping(value = "/monitoring/cluster/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> clusterOverview() {
        Page pageView = pageViewService.getPageView(CLUSTER_OVERVIEW_MENU_ID);
        return responseData(pageView);
    }

    @GetMapping(value = "/monitoring/cluster/workloads/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> clusterWorkloadsOverview() {
        Page pageView = pageViewService.getPageView(CLUSTER_WORKLOADS_OVERVIEW_MENU_ID);
        return responseData(pageView);
    }

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

    @GetMapping(value = "/main", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> dashboard() {
        Map<String, Object> response = responseData(pageViewService.getPageView(MAIN_MENU_ID));
        response.put("services", applicationService);
        return response;
    }

    private Map<String, Object> responseData(Page page) {
        Map<String, Object> response = new HashMap<>();
        response.put("user", getUser());
        response.put("host", monitoringProperties.getDefaultPrometheusUrl());
        response.put("page", page);
        return response;
    }

    private Map<String, Object> lokiresponseData(Page page) {
        Map<String, Object> response = new HashMap<>();
        response.put("user", getUser());
        response.put("host", monitoringProperties.getDefaultCluster().getLoki().getUrl());
        response.put("page", page);
        response.put("services", applicationServiceProperties.getServices().stream()
            .map(ApplicationServiceProperties.Service::getName).collect(Collectors.toList()));
        String joinString = applicationServiceProperties.getServices().stream()
            .map(ApplicationServiceProperties.Service::getName).collect(Collectors.joining("|"));
        response.put("applicationValue", joinString);

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
