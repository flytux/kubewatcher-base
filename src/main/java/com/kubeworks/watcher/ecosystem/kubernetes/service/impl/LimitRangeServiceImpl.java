package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.LimitRangeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.LimitRangeTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1LimitRangeTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.LimitRangeService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1LimitRange;
import io.kubernetes.client.openapi.models.V1LimitRangeList;
import io.kubernetes.client.openapi.models.V1LimitRangeSpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LimitRangeServiceImpl implements LimitRangeService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreV1Api;

    public LimitRangeServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.coreV1Api = new CoreV1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<LimitRangeTable> allNamespaceLimitRangeTables() {
        ApiResponse<V1LimitRangeTableList> apiResponse = coreV1Api.allNamespaceLimitRangeAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1LimitRangeTableList limitRanges = apiResponse.getData();
            return limitRanges.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<LimitRangeDescribe> listNamespacedLimitRange(String namespace) {

        ApiResponse<V1LimitRangeList> apiResponse = coreV1Api.listNamespacedLimitRangeWithHttpInfo(namespace, "true",
            null, null, null, null, ExternalConstants.DEFAULT_K8S_OBJECT_LIMIT,
            null, ExternalConstants.DEFAULT_K8S_CLIENT_TIMEOUT_SECONDS, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Collections.emptyList();
        }

        V1LimitRangeList data = apiResponse.getData();
        List<V1LimitRange> items = data.getItems();

        return items.stream().map(this::setLimitRange).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public Optional<LimitRangeDescribe> limitRange(String namespace, String name) {

        ApiResponse<V1LimitRange> apiResponse = coreV1Api.readNamespacedLimitRangeWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        LimitRangeDescribe.LimitRangeDescribeBuilder builder = LimitRangeDescribe.builder();
        V1LimitRange data = apiResponse.getData();
        setLimitRange(builder, data);

        LimitRangeDescribe limitRangeDescribe = builder.build();

        return Optional.of(limitRangeDescribe);
    }

    private LimitRangeDescribe setLimitRange(V1LimitRange data) {
        LimitRangeDescribe.LimitRangeDescribeBuilder builder = LimitRangeDescribe.builder();
        setLimitRange(builder, data);
        return builder.build();
    }

    private void setLimitRange(LimitRangeDescribe.LimitRangeDescribeBuilder builder, V1LimitRange data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null && CollectionUtils.isNotEmpty(data.getSpec().getLimits())) {
            V1LimitRangeSpec spec = data.getSpec();
            builder.limits(spec.getLimits());
        }
    }
}
