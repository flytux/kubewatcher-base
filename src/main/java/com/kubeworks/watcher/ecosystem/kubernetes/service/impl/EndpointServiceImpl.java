package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EndpointTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EndpointService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1EndpointSubset;
import io.kubernetes.client.openapi.models.V1Endpoints;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EndpointServiceImpl implements EndpointService {

    private final CoreV1ApiExtendHandler coreApi;
    private final EventService eventService;
    private final K8sObjectManager k8sObjectManager;

    @Autowired
    public EndpointServiceImpl(ApiClient k8sApiClient, EventService eventService, K8sObjectManager k8sObjectManager) {
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<EndpointTable> allNamespaceEndpointTables() {
        ApiResponse<V1EndpointTableList> apiResponse = coreApi.searchEndpointsTableList();
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1EndpointTableList endpoints = apiResponse.getData();
            List<EndpointTable> dataTable = endpoints.createDataTableList();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<EndpointTable> endpoints(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceEndpointTables();
        }
        ApiResponse<V1EndpointTableList> apiResponse = coreApi.searchEndpointsTableList(namespace);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1EndpointTableList endpoints = apiResponse.getData();
            return endpoints.createDataTableList();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<EndpointDescribe> endpointWithoutEvent(String namespace, String name) {
        ApiResponse<V1Endpoints> apiResponse = coreApi.readNamespacedEndpointsWithHttpInfo(name, namespace, "true", Boolean.TRUE, Boolean.FALSE);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        EndpointDescribe.EndpointDescribeBuilder builder = EndpointDescribe.builder();
        V1Endpoints data = apiResponse.getData();
        setService(builder, data);

        EndpointDescribe endpointDescribe = builder.build();
        return Optional.of(endpointDescribe);
    }

    @SneakyThrows
    @Override
    public Optional<EndpointDescribe> endpoint(String namespace, String name) {

        Optional<EndpointDescribe> endpointDescribeOptional = endpointWithoutEvent(namespace, name);

        endpointDescribeOptional.ifPresent(endpointDescribe -> {
            Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("Endpoints",
                endpointDescribe.getNamespace(), endpointDescribe.getName(), endpointDescribe.getUid());
            eventTableListOptional.ifPresent(v1EventTableList -> endpointDescribe.setEvents(v1EventTableList.createDataTableList()));
        });

        return endpointDescribeOptional;
    }

    @SneakyThrows
    @Override
    public List<EndpointTable> endpointTable(String namespace, String name) {
        ApiResponse<V1EndpointTableList> apiResponse = coreApi.searchEndpointsTableList(namespace, name);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1EndpointTableList endpoints = apiResponse.getData();
            return endpoints.createDataTableList();
        }
        return Collections.emptyList();
    }

    private void setService(EndpointDescribe.EndpointDescribeBuilder builder, V1Endpoints data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.labels(metadata.getLabels());
            builder.uid(metadata.getUid());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSubsets() != null) {
            List<V1EndpointSubset> subsets = data.getSubsets();
            builder.subsets(subsets);
        }
    }
}
