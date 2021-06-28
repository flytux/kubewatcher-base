package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.LimitRangeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NamespaceDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NamespaceTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ResourceQuotaDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1NamespaceTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.LimitRangeService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.NamespaceService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ResourceQuotaService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceSpec;
import io.kubernetes.client.openapi.models.V1NamespaceStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NamespaceServiceImpl implements NamespaceService {

    private final CoreV1ApiExtendHandler coreV1Api;
    private final ResourceQuotaService resourceQuotaService;
    private final LimitRangeService limitRangeService;
    private final K8sObjectManager k8sObjectManager;

    @Autowired
    public NamespaceServiceImpl(ApiClient k8sApiClient, ResourceQuotaService resourceQuotaService,
                                LimitRangeService limitRangeService, K8sObjectManager k8sObjectManager) {
        this.coreV1Api = new CoreV1ApiExtendHandler(k8sApiClient);
        this.resourceQuotaService = resourceQuotaService;
        this.limitRangeService = limitRangeService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<NamespaceTable> allNamespaceTables() {
        ApiResponse<V1NamespaceTableList> apiResponse = coreV1Api.searchNamespacesTableList();
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1NamespaceTableList namespaces = apiResponse.getData();
            List<NamespaceTable> dataTable = namespaces.getDataTable();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getName(), o2.getName()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<NamespaceDescribe> namespace(String name) {
        ApiResponse<V1Namespace> apiResponse = coreV1Api.readNamespaceWithHttpInfo(name, "true", Boolean.TRUE, Boolean.FALSE);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        NamespaceDescribe.NamespaceDescribeBuilder builder = NamespaceDescribe.builder();
        V1Namespace data = apiResponse.getData();
        setNamespace(builder, data);

        NamespaceDescribe namespaceDescribe = builder.build();

        List<ResourceQuotaDescribe> resourceQuotaDescribes = resourceQuotaService.listNamespacedResourceQuota(namespaceDescribe.getName());
        namespaceDescribe.setResourceQuotas(resourceQuotaDescribes);

        List<LimitRangeDescribe> limitRangeDescribes = limitRangeService.listNamespacedLimitRange(namespaceDescribe.getName());
        namespaceDescribe.setLimitRanges(limitRangeDescribes);

        return Optional.of(namespaceDescribe);
    }

    private void setNamespace(NamespaceDescribe.NamespaceDescribeBuilder builder, V1Namespace data) {

        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            V1NamespaceSpec spec = data.getSpec();
            builder.finalizers(spec.getFinalizers());
        }

        if (data.getStatus() != null) {
            V1NamespaceStatus status = data.getStatus();
            builder.conditions(status.getConditions());
            builder.status(status.getPhase());
        }
    }
}
