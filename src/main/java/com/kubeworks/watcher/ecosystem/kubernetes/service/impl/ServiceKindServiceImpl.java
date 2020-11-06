package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ServiceTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ServiceKindService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ServiceKindServiceImpl implements ServiceKindService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreApi;

    public ServiceKindServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<ServiceTable> allNamespaceServiceTables() {
        ApiResponse<V1ServiceTableList> apiResponse = coreApi.allNamespaceServiceAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ServiceTableList services = apiResponse.getData();
            return services.getDataTable();
        }
        return Collections.emptyList();
    }


}
