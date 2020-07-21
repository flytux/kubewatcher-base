package com.kubeworks.watcher.ecosystem.prometheus.service.impl;

import com.kubeworks.watcher.ecosystem.prometheus.dto.PrometheusApiResponse;
import com.kubeworks.watcher.ecosystem.prometheus.feign.PrometheusFeginClient;
import com.kubeworks.watcher.ecosystem.prometheus.service.PrometheusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class PrometheusServiceImpl implements PrometheusService {

    private final PrometheusFeginClient prometheusFeginClient;

    @Override
    public PrometheusApiResponse requestQuery(String query) {
        return prometheusFeginClient.getQuery(query);
    }

    @Override
    public PrometheusApiResponse requestQueryRange(String query, long startTimestamp, long endTimestamp) {
        // TODO Step 계산식 정의 필요 -- 기준은 Grafana와 동일하게 유지
        return prometheusFeginClient.getQueryRange(query, startTimestamp, endTimestamp, 15);
    }
}
