package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;

import java.util.List;
import java.util.Map;

public interface MetricService {

    List<MetricTable> nodeMetrics();

    MetricTable nodeMetric(String name);

    List<MetricTable> podMetrics();

    List<MetricTable> podMetrics(String namespace, Map<String, String> selector);
}