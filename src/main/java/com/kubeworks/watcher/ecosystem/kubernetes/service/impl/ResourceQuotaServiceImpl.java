package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ResourceQuotaDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ResourceQuotaTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ResourceQuotaTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ResourceQuotaService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResourceQuotaServiceImpl implements ResourceQuotaService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreV1Api;
    private final K8sObjectManager k8sObjectManager;

    public ResourceQuotaServiceImpl(ApiClient k8sApiClient, K8sObjectManager k8sObjectManager) {
        this.k8sApiClient = k8sApiClient;
        this.coreV1Api = new CoreV1ApiExtendHandler(k8sApiClient);
        this.k8sObjectManager = k8sObjectManager;
    }


    @SneakyThrows
    @Override
    public List<ResourceQuotaTable> allNamespaceResourceQuotaTables() {
        ApiResponse<V1ResourceQuotaTableList> apiResponse = coreV1Api.allNamespaceResourceQuotaAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ResourceQuotaTableList resourceQuotas = apiResponse.getData();
            List<ResourceQuotaTable> dataTable = resourceQuotas.getDataTable();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<ResourceQuotaTable> resourceQuotas(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceResourceQuotaTables();
        }

        ApiResponse<V1ResourceQuotaTableList> apiResponse = coreV1Api.namespaceResourceQuotaAsTable(namespace, "true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ResourceQuotaTableList resourceQuotas = apiResponse.getData();
            return resourceQuotas.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<ResourceQuotaDescribe> listNamespacedResourceQuota(String namespace) {
        ApiResponse<V1ResourceQuotaList> apiResponse = coreV1Api.listNamespacedResourceQuotaWithHttpInfo(namespace, "true",
            null, null, null, null, ExternalConstants.DEFAULT_K8S_OBJECT_LIMIT,
            null, ExternalConstants.DEFAULT_K8S_CLIENT_TIMEOUT_SECONDS, null);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Collections.emptyList();
        }

        V1ResourceQuotaList data = apiResponse.getData();
        List<V1ResourceQuota> items = data.getItems();

        return items.stream().map(this::setResourceQuota).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public Optional<ResourceQuotaDescribe> resourceQuota(String namespace, String name) {
        ApiResponse<V1ResourceQuota> apiResponse = coreV1Api.readNamespacedResourceQuotaWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        ResourceQuotaDescribe.ResourceQuotaDescribeBuilder builder = ResourceQuotaDescribe.builder();
        V1ResourceQuota data = apiResponse.getData();
        setResourceQuota(builder, data);

        ResourceQuotaDescribe resourceQuotaDescribe = builder.build();

        return Optional.of(resourceQuotaDescribe);
    }

    private ResourceQuotaDescribe setResourceQuota(V1ResourceQuota data) {
        ResourceQuotaDescribe.ResourceQuotaDescribeBuilder builder = ResourceQuotaDescribe.builder();
        setResourceQuota(builder, data);
        return builder.build();
    }

    private void setResourceQuota(ResourceQuotaDescribe.ResourceQuotaDescribeBuilder builder, V1ResourceQuota data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            V1ResourceQuotaSpec spec = data.getSpec();
            builder.hard(spec.getHard());
            builder.scopes(spec.getScopes());
            V1ScopeSelector scopeSelector = spec.getScopeSelector();
            setScopeSelector(builder, scopeSelector);
        }

        if (data.getStatus() != null) {
            V1ResourceQuotaStatus status = data.getStatus();
            builder.used(status.getUsed());
        }
    }

    private void setScopeSelector(ResourceQuotaDescribe.ResourceQuotaDescribeBuilder builder, V1ScopeSelector scopeSelector) {
        if (scopeSelector == null || CollectionUtils.isEmpty(scopeSelector.getMatchExpressions())) {
            return;
        }

        List<V1ScopedResourceSelectorRequirement> matchExpressions = scopeSelector.getMatchExpressions();

        Map<String, String> scopeSelectMatchExpr = matchExpressions.stream()
            .filter(requirement -> Objects.nonNull(requirement.getValues()))
            .collect(Collectors.toMap(V1ScopedResourceSelectorRequirement::getScopeName, requirement -> {
                String values = String.join(", ", requirement.getValues());
                return values + "(" + requirement.getOperator() + ")";
            }));
        builder.scopeSelector(scopeSelectMatchExpr);
    }
}
