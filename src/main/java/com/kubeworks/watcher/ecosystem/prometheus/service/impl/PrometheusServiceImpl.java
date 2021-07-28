package com.kubeworks.watcher.ecosystem.prometheus.service.impl;

import com.kubeworks.watcher.ecosystem.prometheus.dto.PrometheusApiResponse;
import com.kubeworks.watcher.ecosystem.prometheus.feign.PrometheusFeignClient;
import com.kubeworks.watcher.ecosystem.prometheus.service.PrometheusService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j @Service
public class PrometheusServiceImpl implements PrometheusService {

    private final PrometheusFeignClient client;

    @Autowired
    public PrometheusServiceImpl(final PrometheusFeignClient client) {
        this.client = client;
    }

    @Override
    public PrometheusApiResponse request(final String q) {

        try {
            return client.getQuery(q);
        } catch (final FeignException e) {
            log.error("Feign request failure -> {}", e.getMessage()); log.error("", e);
            return new PrometheusApiResponse();
        }
    }
}
