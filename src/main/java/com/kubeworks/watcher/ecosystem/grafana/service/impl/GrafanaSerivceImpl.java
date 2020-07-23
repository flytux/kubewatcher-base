package com.kubeworks.watcher.ecosystem.grafana.service.impl;

import com.kubeworks.watcher.config.properties.GrafanaProperties;
import com.kubeworks.watcher.config.properties.PrometheusProperties;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.grafana.dto.Dashboard;
import com.kubeworks.watcher.ecosystem.grafana.dto.DashboardDetail;
import com.kubeworks.watcher.ecosystem.grafana.dto.Panel;
import com.kubeworks.watcher.ecosystem.grafana.dto.TemplateVariable;
import com.kubeworks.watcher.ecosystem.grafana.feign.GrafanaFeignClient;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import com.kubeworks.watcher.ecosystem.prometheus.dto.PrometheusApiResponse;
import com.kubeworks.watcher.ecosystem.prometheus.service.PrometheusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class GrafanaSerivceImpl implements GrafanaSerivce {

    private final PrometheusProperties prometheusProperties;
    private final GrafanaFeignClient grafanaFeignClient;
    private final GrafanaProperties grafanaProperties;
    private final PrometheusService prometheusService;

    @Override
    public List<Dashboard> dashboards() {
        List<Dashboard> dashboards = grafanaFeignClient.getDashboards();
        log.info("dashboard list size = {}", dashboards.size());
        return dashboards;
    }

    @Override
    public DashboardDetail dashboard(String uid) {
        DashboardDetail dashboardDetail = grafanaFeignClient.getDashboard(uid);
        dashboardDetail.getMeta().setHost(grafanaProperties.getUrl());
        dashboardDetail.getMeta().setPromHost(prometheusProperties.getUrl());
        settingTemplateVariable(dashboardDetail.getDashboard());
        log.debug("dashboard = {}", dashboardDetail.toString());
        return dashboardDetail;
    }

    @Override
    public Panel panel(String dashboardUid, String panelUid) {
        return null;
    }

    private void settingTemplateVariable(Dashboard dashboard) {
        Map<String, TemplateVariable> templating = dashboard.getTemplating();
        // set refFields
        templating.values()
            .forEach(var -> var.setRefFields(ExternalConstants.getTemplateVariables(var.getQuery())));

        // sorting & setValues
        templating.values().stream()
            .filter(var -> var.getRefFields().isEmpty())
            .sorted(ExternalConstants::compareSortTemplateVariables)
            .forEach(var -> {
                if (!StringUtils.equalsIgnoreCase("query", var.getType())) {
                    // 현재 버전에서는 query만 정의하고, 기능 구현시 나머지 형식에 대해서도 고려한다.
                    return;
                }
                PrometheusApiResponse response = prometheusService.requestQuery(var.getApiQuery());
                if (!StringUtils.equals(ExternalConstants.SUCCESS_STATUS_STR, response.getStatus().toLowerCase())) {
                    return;
                }

                List<String> values = response.getData().getResult().stream()
                    .map(PrometheusApiResponse.PrometheusApiData.PrometheusApiResult::getMetric)
                    .map(metric -> metric.get(var.getApiQueryMetricLabel()))
                    .distinct()
                    .collect(Collectors.toList());

                var.setValues(values);
            });
    }

}
