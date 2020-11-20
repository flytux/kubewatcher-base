package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.IngressTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1beta1IngressTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.NetworkingV1beta1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.IngressService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class IngressServiceImpl implements IngressService {

    private final ApiClient k8sApiClient;
    private final NetworkingV1beta1ApiExtendHandler networkingApi;

    public IngressServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.networkingApi = new NetworkingV1beta1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<IngressTable> allNamespaceIngressTables() {
        ApiResponse<NetworkingV1beta1IngressTableList> apiResponse = networkingApi.allNamespaceIngressAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            NetworkingV1beta1IngressTableList ingresses = apiResponse.getData();
            return ingresses.getDataTable();
        }
        return Collections.emptyList();
    }
}
