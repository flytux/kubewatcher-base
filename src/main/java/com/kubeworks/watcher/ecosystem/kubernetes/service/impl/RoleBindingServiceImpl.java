package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.RbacV1beta1RoleBindingTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.RbacV1beta1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.RoleBindingService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class RoleBindingServiceImpl implements RoleBindingService {

    private final ApiClient k8sApiClient;
    private final RbacV1beta1ApiExtendHandler rbacApi;

    public RoleBindingServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.rbacApi = new RbacV1beta1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<RoleBindingTable> allNamespaceRoleBindingTables() {
        ApiResponse<RbacV1beta1RoleBindingTableList> apiResponse = rbacApi.allNamespaceRoleBindingAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            RbacV1beta1RoleBindingTableList roleBindings = apiResponse.getData();
            return roleBindings.getDataTable();
        }
        return Collections.emptyList();
    }


}
