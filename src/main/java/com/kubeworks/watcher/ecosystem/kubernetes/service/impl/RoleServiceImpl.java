package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.RbacV1RoleTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.RbacV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.RoleService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final ApiClient k8sApiClient;
    private final RbacV1ApiExtendHandler rbacV1Api;
    private final EventService eventService;

    public RoleServiceImpl(ApiClient k8sApiClient,EventService eventService) {
        this.k8sApiClient = k8sApiClient;
        this.rbacV1Api = new RbacV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
    }

    @SneakyThrows
    @Override
    public List<RoleTable> allNamespaceRoleTables() {
        ApiResponse<RbacV1RoleTableList> apiResponse = rbacV1Api.allNamespaceRoleAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            RbacV1RoleTableList secrets = apiResponse.getData();
            return secrets.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<RoleDescribe> role(String namespace, String name) {
        ApiResponse<V1Role> apiResponse = rbacV1Api.readNamespacedRoleWithHttpInfo(name, namespace, "true");

        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        RoleDescribe.RoleDescribeBuilder builder = RoleDescribe.builder();
        V1Role data = apiResponse.getData();
        setRole(builder, data);

        RoleDescribe roleDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("Role",
            roleDescribe.getNamespace(), roleDescribe.getName(), roleDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> roleDescribe.setEvents(v1EventTableList.getDataTable()));

        return Optional.of(roleDescribe);
    }
    private void setRole(RoleDescribe.RoleDescribeBuilder builder, V1Role data) {

        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getRules() != null) {
            builder.rules(data.getRules());
        }

    }
}
