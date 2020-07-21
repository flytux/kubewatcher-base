package com.kubeworks.watcher.ecosystem.grafana.feign;

import com.kubeworks.watcher.config.RestClientsConfig;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.grafana.dto.Dashboard;
import com.kubeworks.watcher.ecosystem.grafana.dto.DashboardDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "grafana", url = "${application.properties.dependencies.grafana.url}",
    configuration = RestClientsConfig.GrafanaFeignClientConfig.class)
public interface GrafanaFeignClient {


    @GetMapping(value = ExternalConstants.GRAFANA_DASHBOARD_ALL_SEARCH_URI)
    List<Dashboard> getDashboards();

    @GetMapping(value = ExternalConstants.GRAFANA_DASHBOARD_ALL_SEARCH_URI)
    List<Dashboard> searchDashboards(@RequestParam("query") String query);

    @GetMapping(value = ExternalConstants.GRAFANA_DASHBOARD_GET_URI_PATTERN)
    DashboardDetail getDashboard(@PathVariable("uid") String uid);



}
