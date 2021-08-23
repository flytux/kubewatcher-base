package com.kubeworks.watcher.cloud.monitoring.controller;

import com.google.common.collect.ImmutableMap;
import com.kubeworks.watcher.alarm.service.AlertAlarmListService;
import com.kubeworks.watcher.cloud.monitoring.service.PageMetricService;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/api/v1")
@AllArgsConstructor(onConstructor_={@Autowired})
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

    private static final String SERVICES_STR = "services";

    private final PageViewService pageViewService;
    private final MonitoringProperties monitoringProperties;
    private final PageMetricService<Page> applicationPageMetricService;
    private final ApplicationService applicationService;
    private final AlertAlarmListService alertAlarmListService;

    private final ApplicationService.ApplicationManagementHandler handler;

    @GetMapping(value="/monitoring/application/overview")
    public Map<String, Object> application() {

        final Map<String, Object> response = responseData(applicationPageMetricService.pageMetrics(APPLICATION_OVERVIEW_MENU_ID));
        response.put(SERVICES_STR, applicationService);

        return response;
    }

    @GetMapping(value="/monitoring/cluster/overview")
    public Map<String, Object> clusterOverview() {
        return responseData(pageViewService.getPageView(CLUSTER_OVERVIEW_MENU_ID));
    }

    @GetMapping(value="/monitoring/cluster/workloads/overview")
    public Map<String, Object> clusterWorkloadsOverview() {
        return responseData(pageViewService.getPageView(CLUSTER_WORKLOADS_OVERVIEW_MENU_ID));
    }

    @GetMapping(value="/monitoring/database")
    public Map<String, Object> database() {
        return responseData(pageViewService.getPageView(DATABASE_MENU_ID));
    }

    @GetMapping(value="/monitoring/jvm/overview")
    public Map<String, Object> jvmOverview() {
        return responseData(pageViewService.getPageView(JVM_OVERVIEW_MENU_ID));
    }

    @GetMapping(value="/monitoring/jvm/application")
    public Map<String, Object> jvmDetail() {
        return responseData(pageViewService.getPageView(JVM_DETAIL_MENU_ID));
    }

    @GetMapping(value="/monitoring/vm/overview")
    public Map<String, Object> vmOverview() {
        return responseData(pageViewService.getPageView(VM_OVERVIEW_MENU_ID));
    }

    @GetMapping(value="/monitoring/vm/monitoring")
    public Map<String, Object> vmDetail() {
        return responseData(pageViewService.getPageView(VM_DETAIL_MENU_ID));
    }

    @GetMapping(value="/main")
    public Map<String, Object> dashboard() {

        final Map<String, Object> response = responseData(pageViewService.getPageView(MAIN_MENU_ID));
        response.put(SERVICES_STR, applicationService);
        response.put("alertHistoryList", alertAlarmListService.alertPageHistory(1, null, null, null, null, null, null, 20));

        return response;
    }

    @GetMapping(value="/monitoring/logging")
    public Map<String, Object> logging() {
        return lokiresponseData(pageViewService.getPageView(LOGGING_MENU_ID));
    }

    private Map<String, Object> responseData(final Page page) {

        final Map<String, Object> response = new HashMap<>();

        response.put("host", monitoringProperties.getDefaultPrometheusUrl());
        response.put("page", page);

        return response;
    }

    private Map<String, Object> lokiresponseData(final Page page) {

        final Map<String, Object> response = new HashMap<>();

        response.put("host", monitoringProperties.getDefaultCluster().getLoki().getUrl());
        response.put("page", page);
        response.put(SERVICES_STR, applicationService.getManagementByName());
        response.put("applicationValue", applicationService.getServiceNamesLoki());

        return response;
    }

    @DeleteMapping(value="/monitoring/cache")
    public Map<String, String> clearApplicationManagementCache() {
        handler.clearCache();
        return ImmutableMap.of("res", "success");
    }
}
