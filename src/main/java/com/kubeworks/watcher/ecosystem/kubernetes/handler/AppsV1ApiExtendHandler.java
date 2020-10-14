package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AppsV1DaemonSetTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AppsV1DeploymentTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AppsV1StatefulSetTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

@Slf4j
public class AppsV1ApiExtendHandler extends AppsV1Api implements BaseExtendHandler {

    public AppsV1ApiExtendHandler(ApiClient apiClient) {
        super(apiClient);
    }

    public ApiResponse<AppsV1DeploymentTableList> allNamespaceDeploymentAsTable(String pretty) throws ApiException {
        Call call = listDeploymentAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(AppsV1DeploymentTableList.class).getType());
    }

    public ApiResponse<AppsV1DaemonSetTableList> allNamespaceDaemonSetAsTable(String pretty) throws ApiException {
        Call call = listDaemonSetAsTablesForAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(AppsV1DaemonSetTableList.class).getType());
    }

    public ApiResponse<AppsV1StatefulSetTableList> allNamespaceStatefulSetAsTable(String pretty) throws ApiException {
        Call call = listStatefulSetAsTablesForAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(AppsV1StatefulSetTableList.class).getType());
    }

    public Call listDeploymentAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/apps/v1/deployments";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listDaemonSetAsTablesForAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/apps/v1/daemonsets";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listStatefulSetAsTablesForAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/apps/v1/statefulsets";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }



}
