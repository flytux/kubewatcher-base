package com.kubeworks.watcher.ecosystem.prometheus.service;

import com.kubeworks.watcher.ecosystem.prometheus.dto.PrometheusApiResponse;

public interface PrometheusService {

    PrometheusApiResponse requestQuery(String query);

    PrometheusApiResponse requestQueryRange(String query, long startTimestamp, long endTimestamp);

}
