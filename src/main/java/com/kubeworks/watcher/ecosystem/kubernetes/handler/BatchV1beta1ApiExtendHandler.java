package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.BatchV1beta1CronJobTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.BatchV1beta1Api;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

public class BatchV1beta1ApiExtendHandler extends BatchV1beta1Api implements BaseExtendHandler {

    public BatchV1beta1ApiExtendHandler(ApiClient apiClient) {
        super(apiClient);
    }

    public ApiResponse<BatchV1beta1CronJobTableList> allNamespaceCronJobAsTable(String pretty) throws ApiException {
        Call call = listCronJobAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(BatchV1beta1CronJobTableList.class).getType());
    }

    public ApiResponse<BatchV1beta1CronJobTableList> namespaceCronJobAsTable(String namespace, String pretty) throws ApiException {
        Call call = listCronJobAsTableNamespacesCall(namespace, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(BatchV1beta1CronJobTableList.class).getType());
    }

    public Call listCronJobAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/batch/v1beta1/cronjobs";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listCronJobAsTableNamespacesCall(String namespace, String pretty) throws ApiException {
        String localVarPath = "/apis/batch/v1beta1/namespaces/{namespace}/cronjobs".replaceAll("\\{" + "namespace" + "}", namespace);;
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }
}
