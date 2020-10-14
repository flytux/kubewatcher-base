package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.StorageV1StorageClassTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.StorageV1Api;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

public class StorageV1ApiExtendHandler extends StorageV1Api implements BaseExtendHandler {

    public StorageV1ApiExtendHandler(ApiClient k8sApiClient) {
        super(k8sApiClient);
    }

    public ApiResponse<StorageV1StorageClassTableList> allNamespaceStorageClassAsTable(String pretty) throws ApiException {
        Call call = listStorageClassAsTableCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(StorageV1StorageClassTableList.class).getType());
    }

    public Call listStorageClassAsTableCall(String pretty) throws ApiException {
        String localVarPath = "/apis/storage.k8s.io/v1/storageclasses";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }
}
