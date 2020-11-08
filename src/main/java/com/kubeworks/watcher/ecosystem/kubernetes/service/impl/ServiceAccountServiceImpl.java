package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceAccountTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ServiceAccountTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ServiceAccountService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ServiceAccountServiceImpl implements ServiceAccountService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreApi;

    public ServiceAccountServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<ServiceAccountTable> allNamespaceServiceAccountTables() {
        ApiResponse<V1ServiceAccountTableList> apiResponse = coreApi.allNamespaceServiceAccountAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ServiceAccountTableList serviceAccounts = apiResponse.getData();
            return serviceAccounts.getDataTable();
        }
        return Collections.emptyList();
    }
}
