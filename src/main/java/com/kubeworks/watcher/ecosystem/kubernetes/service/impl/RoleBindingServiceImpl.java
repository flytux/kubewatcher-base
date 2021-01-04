package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingTable;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.RbacV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.RoleBindingService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1RoleBinding;
import io.kubernetes.client.openapi.models.V1RoleBindingList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleBindingServiceImpl implements RoleBindingService {

    private final ApiClient k8sApiClient;
    private final RbacV1ApiExtendHandler rbacApi;
    private final K8sObjectManager k8sObjectManager;

    public RoleBindingServiceImpl(ApiClient k8sApiClient, K8sObjectManager k8sObjectManager) {
        this.k8sApiClient = k8sApiClient;
        this.rbacApi = new RbacV1ApiExtendHandler(k8sApiClient);
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<RoleBindingTable> allNamespaceRoleBindingTables() {
//        ApiResponse<RbacV1RoleBindingTableList> apiResponse = rbacApi.allNamespaceRoleBindingAsTables("true");
        ApiResponse<V1RoleBindingList> apiResponse = rbacApi.listRoleBindingForAllNamespacesWithHttpInfo(null, null, null, null, ExternalConstants.DEFAULT_K8S_OBJECT_LIMIT, "false", null, ExternalConstants.DEFAULT_K8S_CLIENT_TIMEOUT_SECONDS, null);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1RoleBindingList data = apiResponse.getData();
            return data.getItems().stream()
                .map(this::convertRoleBindingTable)
                .sorted((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()))
                .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<RoleBindingTable> roleBindings(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceRoleBindingTables();
        }
        ApiResponse<V1RoleBindingList> apiResponse = rbacApi.listNamespacedRoleBindingWithHttpInfo(namespace, "true", null, null, null, null, ExternalConstants.DEFAULT_K8S_OBJECT_LIMIT, null, ExternalConstants.DEFAULT_K8S_CLIENT_TIMEOUT_SECONDS, null);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1RoleBindingList data = apiResponse.getData();
            return data.getItems().stream().map(this::convertRoleBindingTable).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<RoleBindingDescribe> roleBinding(String namespace, String name) {
        ApiResponse<V1RoleBinding> apiResponse = rbacApi.readNamespacedRoleBindingWithHttpInfo(name, namespace, "true");
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        RoleBindingDescribe.RoleBindingDescribeBuilder builder = RoleBindingDescribe.builder();
        V1RoleBinding data = apiResponse.getData();
        setRoleBinding(builder, data);

        RoleBindingDescribe roleBindingDescribe = builder.build();

        return Optional.of(roleBindingDescribe);
    }

    private RoleBindingTable convertRoleBindingTable(V1RoleBinding v1RoleBinding) {
        RoleBindingTable roleBindingTable = new RoleBindingTable();
        if (v1RoleBinding.getMetadata() != null) {
            roleBindingTable.setName(v1RoleBinding.getMetadata().getName());
            roleBindingTable.setNamespace(v1RoleBinding.getMetadata().getNamespace());
            if (v1RoleBinding.getMetadata().getCreationTimestamp() != null) {
                String age = ExternalConstants.getBetweenPeriodDay(v1RoleBinding.getMetadata().getCreationTimestamp().toInstant().getMillis());
                roleBindingTable.setAge(age);
            }
        }

        if (v1RoleBinding.getRoleRef() != null) {
            String role = v1RoleBinding.getRoleRef().getKind() + "/" + v1RoleBinding.getRoleRef().getName();
            roleBindingTable.setRole(role);
        }
        return roleBindingTable;
    }

    private void setRoleBinding(RoleBindingDescribe.RoleBindingDescribeBuilder builder, V1RoleBinding data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getRoleRef() != null) {
            builder.roleRefs(data.getRoleRef());
        }

        if (data.getSubjects() != null) {
            builder.subjects(data.getSubjects());
        }
    }
}
