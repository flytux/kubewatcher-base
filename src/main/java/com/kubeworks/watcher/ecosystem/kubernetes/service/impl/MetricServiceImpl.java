package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NodeMetrics;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1NodeMetricTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1PodMetricTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.MetricService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MetricServiceImpl implements MetricService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreV1Api;

    public MetricServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.coreV1Api = new CoreV1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<MetricTable> nodeMetrics() {
        ApiResponse<V1NodeMetricTableList> apiResponse = coreV1Api.listMetricNodeAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1NodeMetricTableList metric = apiResponse.getData();
            return metric.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public MetricTable nodeMetric(String name) {
        ApiResponse<NodeMetrics> apiResponse = coreV1Api.metricNode(name, "true");
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
        ApiResponse<V1PodMetricTableList> apiResponse = coreV1Api.metricPodAsTable("true",null,null);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1PodMetricTableList metric = apiResponse.getData();
            return metric.getDataTable();

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

        ApiResponse<V1PodMetricTableList> apiResponse = coreV1Api.namespacePodMetricAsTable(namespace,null, labelSelectors, "true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1PodMetricTableList metric = apiResponse.getData();
            return metric.getDataTable();
        }
        return Collections.emptyList();
    }
}
