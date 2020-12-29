package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.BatchV1JobTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

public class BatchV1ApiExtendHandler extends BatchV1Api implements BaseExtendHandler {

    public BatchV1ApiExtendHandler(ApiClient k8sApiClient) {
        super(k8sApiClient);
    }

    public ApiResponse<BatchV1JobTableList> allNamespaceJobAsTable(String pretty) throws ApiException {
        Call call = listJobAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(BatchV1JobTableList.class).getType());
    }

    public ApiResponse<BatchV1JobTableList> namespaceJobAsTable(String namespace, String pretty) throws ApiException {
        Call call = listJobAsTableNamespacesCall(namespace, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(BatchV1JobTableList.class).getType());
    }

    public Call listJobAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/batch/v1/jobs";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listJobAsTableNamespacesCall(String namespace, String pretty) throws ApiException {
        String localVarPath = "/apis/batch/v1/namespaces/{namespace}/jobs".replaceAll("\\{" + "namespace" + "}", namespace);
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }
}
