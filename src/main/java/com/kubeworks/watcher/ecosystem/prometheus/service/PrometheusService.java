package com.kubeworks.watcher.ecosystem.prometheus.service;

import com.kubeworks.watcher.ecosystem.prometheus.dto.PrometheusApiResponse;

public interface PrometheusService {
    PrometheusApiResponse request(final String q);
}
