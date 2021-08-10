package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.kubeworks.watcher.base.MetricResponseData;
import com.kubeworks.watcher.data.mapper.ClusterPodUsageMapper;
import com.kubeworks.watcher.data.vo.ClusterPodUsage;
import com.kubeworks.watcher.data.vo.UsageMetricType;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NodeMetrics;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1NodeMetricTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1PodMetricTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.MetricService;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class MetricServiceImpl implements MetricService {

    private static final String H2_FORMATDATE = "FORMATDATETIME(create_time, '%s')";
    private static final String MYSQL_FORMATDATE = "DATE_FORMAT(create_time, '%s')";

    private final CoreV1ApiExtendHandler coreV1Api;
    private final ClusterPodUsageMapper clusterPodUsageMapper;

    private final ApplicationService applicationService;

    @Value("${spring.jpa.database}")
    public String databaseEngine;

    public MetricServiceImpl(ApiClient k8sApiClient, ClusterPodUsageMapper clusterPodUsageMapper, ApplicationService applicationService) {
        this.coreV1Api = new CoreV1ApiExtendHandler(k8sApiClient);
        this.clusterPodUsageMapper = clusterPodUsageMapper;
        this.applicationService = applicationService;
    }

    @SneakyThrows
    @Override
    public List<MetricTable> nodeMetrics() {
        ApiResponse<V1NodeMetricTableList> apiResponse = coreV1Api.searchNodeMetricTableList();
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1NodeMetricTableList metric = apiResponse.getData();
            return metric.createDataTableList();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public MetricTable nodeMetric(String name) {
        ApiResponse<NodeMetrics> apiResponse = coreV1Api.searchNodeMetric(name);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {

            NodeMetrics node = apiResponse.getData();
            MetricTable metricTable = new MetricTable();

            metricTable.setName(node.getMetadata().getName());
            metricTable.setCpu(node.getUsage().get("cpu"));
            metricTable.setMemory(node.getUsage().get("memory"));

            return metricTable;

        }
        return null;
    }

    @SneakyThrows
    @Override
    public List<MetricTable> podMetrics() {
        ApiResponse<V1PodMetricTableList> apiResponse = coreV1Api.searchPodMetricTableList(null, null);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1PodMetricTableList metric = apiResponse.getData();
            return metric.createDataTableList();

        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<MetricTable> podMetrics(String namespace, Map<String, String> selector) {

        // TODO matchExpressions 일 경우 확인이 필요함.
        String labelSelectors = selector.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(","));

        ApiResponse<V1PodMetricTableList> apiResponse = coreV1Api.searchPodMetricTableList(namespace, null, labelSelectors);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1PodMetricTableList metric = apiResponse.getData();
            return metric.createDataTableList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<ClusterPodUsage> usages(String namespace, LocalDateTime start, LocalDateTime end) {

        if (start == null || end == null) {
            LocalDateTime now = LocalDateTime.now();
            start = now.minusHours(1);
            end = now;
        }

        if (StringUtils.equalsIgnoreCase(namespace, "all")) {
            return clusterPodUsageMapper.selectSummaryByNamespacePeriod(null, start, end);
        }

        return clusterPodUsageMapper.selectSummaryByNamespacePeriod(namespace, start, end);
    }

    @Override
    public com.kubeworks.watcher.base.ApiResponse<MetricResponseData> usageMetrics(String namespace, String application, UsageMetricType usageMetricType,
                                                                                   ChronoUnit unit, LocalDateTime start) {
        com.kubeworks.watcher.base.ApiResponse<MetricResponseData> response = new com.kubeworks.watcher.base.ApiResponse<>();
        try {

            LocalDateTime end;
            if (unit == ChronoUnit.DAYS) {
                start = start.truncatedTo(ChronoUnit.DAYS);
                end = start.plusDays(1).minusNanos(1);
            } else if (unit == ChronoUnit.MONTHS) {
                start = LocalDateTime.of(start.getYear(), start.getMonth(), 1, 0, 0);
                end = start.plusMonths(1).minusNanos(1);
            } else {
                throw new IllegalArgumentException("unsupported unit // unit=" + unit);
            }

            String groupByFormat = getUsageGroupByFormat(unit);
            List<ClusterPodUsage> clusterPodUsages = clusterPodUsageMapper.selectStatisticsSummaryByPeriodGroup(namespace, application, usageMetricType, groupByFormat, start, end);
            MetricResponseData.MetricResult maxMetric = getMetricValue(usageMetricType, clusterPodUsages, "Max " + usageMetricType.name(), true);
            MetricResponseData.MetricResult avgMetric = getMetricValue(usageMetricType, clusterPodUsages, "Avg " + usageMetricType.name(), false);
            MetricResponseData metricResponseData = new MetricResponseData(ImmutableList.of(maxMetric, avgMetric));
            metricResponseData.setResultType("matrix");
            response.setData(metricResponseData);
        } catch (Exception e) {
            log.error("failed get usage metrics", e);
            response.setSuccess(false);
            response.setMessage(e.getMessage());

        }
        return response;
    }

    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Seoul")
    public void aggregateUsageByManagementApplication() {
        final long startMs = System.currentTimeMillis();
        final LocalDateTime now = LocalDateTime.now();
        List<MetricTable> metricTables = podMetrics();
        List<ClusterPodUsage> aggregateUsage = metricTables.stream()
            .filter(metricTable -> CollectionUtils.isNotEmpty(applicationService.getManagementByNamespace().get(metricTable.getNamespace())))
            .map(metricTable -> createClusterPodUsage(now, metricTable))
            .collect(Collectors.groupingBy(ClusterPodUsage::getApplication))
            .values().stream()
            .map(value -> value.stream().reduce(this::accumulateClusterPodUsage))
            .flatMap(clusterPodUsage -> clusterPodUsage.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toList());

        log.debug("end={} // count={}, aggregateUsage={}", System.currentTimeMillis() - startMs,
            aggregateUsage.size(), aggregateUsage);

        if (!aggregateUsage.isEmpty()) {
            clusterPodUsageMapper.inserts(aggregateUsage); return;
        }

        log.debug("skip database insert operation -> usage data size 0");
    }

    private String getUsageGroupByFormat(ChronoUnit unit) {

        if (StringUtils.equalsIgnoreCase(databaseEngine, "h2")) {
            if (unit == ChronoUnit.DAYS) {
                return String.format(H2_FORMATDATE, "yyyy-MM-dd HH:00:00");
            }
            if (unit == ChronoUnit.MONTHS) {
                return String.format(H2_FORMATDATE, "yyyy-MM-dd 00:00:00");
            }
        } else if (StringUtils.equalsIgnoreCase(databaseEngine, "mysql")) {
            if (unit == ChronoUnit.DAYS) {
                return String.format(MYSQL_FORMATDATE, "%Y-%m-%d %h:00:00");
            }
            if (unit == ChronoUnit.MONTHS) {
                return String.format(MYSQL_FORMATDATE, "%Y-%m-%d 00:00:00");
            }
        }

        throw new IllegalArgumentException("unsupported unit // unit=" + unit);
    }

    private MetricResponseData.MetricResult getMetricValue(UsageMetricType usageMetricType, List<ClusterPodUsage> clusterPodUsages, String metricName, boolean isMax) {
        return MetricResponseData.MetricResult.builder()
            .metric(ImmutableMap.of("name", metricName))
            .values(clusterPodUsages.stream()
                .map(clusterPodUsage -> getUsageMetricValues(clusterPodUsage, usageMetricType, isMax))
                .collect(Collectors.toList())).build();
    }

    private List<Object> getUsageMetricValues(ClusterPodUsage clusterPodUsage, UsageMetricType usageMetricType, boolean isMax) {
        long epochMilli = clusterPodUsage.getCreateTime()
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;

        if (usageMetricType == UsageMetricType.POD) {
            if (isMax) {
                return ImmutableList.of(epochMilli, clusterPodUsage.getMaxPodCount());
            }
            return ImmutableList.of(epochMilli, clusterPodUsage.getPodCount());
        } else if (usageMetricType == UsageMetricType.CPU) {
            if (isMax) {
                return ImmutableList.of(epochMilli, clusterPodUsage.getMaxCpu().getNumber());
            }
            return ImmutableList.of(epochMilli, clusterPodUsage.getAvgCpu().getNumber());
        } else if (usageMetricType == UsageMetricType.MEMORY) {
            if (isMax) {
                return ImmutableList.of(epochMilli, clusterPodUsage.getMaxMemory().getNumber());
            }
            return ImmutableList.of(epochMilli, clusterPodUsage.getAvgMemory().getNumber());
        } else {
            log.warn("Unexpected type -> {}", usageMetricType);
        }

        return Collections.emptyList();
    }


    private ClusterPodUsage createClusterPodUsage(LocalDateTime now, MetricTable metricTable) {
        return ClusterPodUsage.builder()
            .namespace(metricTable.getNamespace())
            .application(getApplicationName(metricTable.getName()))
            .cpu(metricTable.getCpu())
            .memory(metricTable.getMemory())
            .createTime(now)
            .build();
    }

    private String getApplicationName(String name) {
        String[] split = StringUtils.split(name, ExternalConstants.UNKNOWN_DASH);
        if (ArrayUtils.isEmpty(split)) {
            return ExternalConstants.NONE_STR;
        }

        if (split.length == 1) {
            return name;
        }
        return Arrays.stream(split, 0, split.length - 2).collect(Collectors.joining(ExternalConstants.UNKNOWN_DASH));
    }

    private ClusterPodUsage accumulateClusterPodUsage(ClusterPodUsage usage1, ClusterPodUsage usage2) {
        usage1.addCpu(usage2.getCpu());
        usage1.addMemory(usage2.getMemory());
        usage1.addPodCount(usage2.getPodCount());
        return usage1;
    }
}
