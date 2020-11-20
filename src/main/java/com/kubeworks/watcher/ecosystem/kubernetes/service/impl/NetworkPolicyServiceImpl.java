package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1NetworkPolicyTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.NetworkingV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.NetworkPolicyService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class NetworkPolicyServiceImpl implements NetworkPolicyService {

    private final ApiClient k8sApiClient;
    private final NetworkingV1ApiExtendHandler networkApi;

    public NetworkPolicyServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.networkApi = new NetworkingV1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<NetworkPolicyTable> allNamespaceNetworkPolicyTables() {
        ApiResponse<NetworkingV1NetworkPolicyTableList> apiResponse = networkApi.allNamespaceNetworkPolicyAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            NetworkingV1NetworkPolicyTableList networkpolicies = apiResponse.getData();
            return networkpolicies.getDataTable();
        }
        return Collections.emptyList();
    }
}
