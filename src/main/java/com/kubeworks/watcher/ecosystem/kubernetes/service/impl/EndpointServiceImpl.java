package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EndpointTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EndpointService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class EndpointServiceImpl implements EndpointService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreApi;

    public EndpointServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<EndpointTable> allNamespaceEndpointTables() {
        ApiResponse<V1EndpointTableList> apiResponse = coreApi.allNamespaceEndpointAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1EndpointTableList endpoints = apiResponse.getData();
            return endpoints.getDataTable();
        }
        return Collections.emptyList();
    }
}
