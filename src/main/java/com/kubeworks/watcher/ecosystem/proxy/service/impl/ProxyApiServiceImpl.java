package com.kubeworks.watcher.ecosystem.proxy.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import com.kubeworks.watcher.ecosystem.prometheus.dto.PrometheusApiResponse;
import com.kubeworks.watcher.ecosystem.prometheus.service.PrometheusService;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Slf4j
@Service(value = "proxyApiService")
public class ProxyApiServiceImpl implements ProxyApiService {

    private final PrometheusService prometheusService;
    private final GrafanaSerivce grafanaSerivce;

    public ProxyApiServiceImpl(PrometheusService prometheusService, GrafanaSerivce grafanaSerivce) {
        this.prometheusService = prometheusService;
        this.grafanaSerivce = grafanaSerivce;
    }

    /*
        TODO 고려 사항 : 현재는 test와 화면 초안 구성을 위해 특정 사항(promql, 단일 응답(String)으로만 처리함.)
            - request :
                - api 구분값이 필요함. (Prometheus or K8S ...)
                - [PromQL] query or range_query인지 구분할 수 있어야 함.
            - response :
                - 응답값이 단일 값인지 혹은 리스트 인지, 타임라인 챠트 데이터 인지...
     */
    @Override
    public String singleValueByQuery(String query) {
        PrometheusApiResponse response = prometheusService.requestQuery(query);
        if (!StringUtils.equalsIgnoreCase(response.getStatus(), ExternalConstants.SUCCESS_STATUS_STR)) {
            return ExternalConstants.NONE_STR;
        }
        return singleValue(response);
    }

    @Override
    public List<String> labelValuesQuery(String query) {

        Matcher matcher = ExternalConstants.GRAFANA_TEMPLATE_VARIABLE_PATTERN.matcher(query);
        if (matcher.find()) {
            return Collections.emptyList();
        }

        final String promQl = StringUtils.substring(query, query.indexOf("label_values(") + "label_values(".length(), query.lastIndexOf(',')).trim();
        final String metricName = StringUtils.substring(query, query.lastIndexOf(',') + 1, query.lastIndexOf(')')).trim();

        PrometheusApiResponse response = prometheusService.requestQuery(promQl);

        if (!StringUtils.equalsIgnoreCase(response.getStatus(), ExternalConstants.SUCCESS_STATUS_STR)) {
            return Collections.singletonList(ExternalConstants.NONE_STR);
        }
        return response.getData().getResult().stream()
            .map(PrometheusApiResponse.PrometheusApiData.PrometheusApiResult::getMetric)
            .map(metric -> metric.get(metricName))
            .filter(Objects::nonNull)
            .distinct()
            .sorted(String::compareTo)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> multiValuesQuery(String query, String metricName) {

        PrometheusApiResponse response = prometheusService.requestQuery(query);

        if (!StringUtils.equalsIgnoreCase(response.getStatus(), ExternalConstants.SUCCESS_STATUS_STR)) {
            return Collections.singletonList(ExternalConstants.NONE_STR);
        }
        return response.getData().getResult().stream()
            .map(PrometheusApiResponse.PrometheusApiData.PrometheusApiResult::getMetric)
            .map(metric -> metric.get(metricName))
            .filter(Objects::nonNull)
            .distinct()
            .sorted(String::compareTo)
            .collect(Collectors.toList());
    }

    private String singleValue(PrometheusApiResponse prometheusApiResponse) {
        List<PrometheusApiResponse.PrometheusApiData.PrometheusApiResult> result = prometheusApiResponse.getData().getResult();
        if (result.isEmpty()) {
            return ExternalConstants.NONE_STR;
        }
        if (result.get(0).getValue().isEmpty()) {
            return ExternalConstants.NONE_STR;
        }

        return String.valueOf(result.get(0).getValue().get(1));
    }
}
