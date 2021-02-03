package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.base.MetricResponseData;
import com.kubeworks.watcher.data.vo.ClusterPodUsage;
import com.kubeworks.watcher.data.vo.UsageMetricType;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public interface MetricService {

    List<MetricTable> nodeMetrics();

    MetricTable nodeMetric(String name);

    List<MetricTable> podMetrics();

    List<MetricTable> podMetrics(String namespace, Map<String, String> selector);

    List<ClusterPodUsage> usages(String namespace, LocalDateTime start, LocalDateTime end);

    ApiResponse<MetricResponseData> usageMetrics(String namespace, String application, UsageMetricType usageMetricType, ChronoUnit unit, LocalDateTime start);
}
