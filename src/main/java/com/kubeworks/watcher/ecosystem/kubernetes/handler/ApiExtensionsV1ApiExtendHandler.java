package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.ApiExtV1CustomResourceTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.ApiextensionsV1Api;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

public class ApiExtensionsV1ApiExtendHandler extends ApiextensionsV1Api implements BaseExtendHandler {

    public ApiExtensionsV1ApiExtendHandler(ApiClient k8sApiClient) {
        super(k8sApiClient);
    }

    public ApiResponse<ApiExtV1CustomResourceTableList> allCustomResourceAsTable(String pretty) throws ApiException {
        Call call = listCustomResourceAsTableCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(ApiExtV1CustomResourceTableList.class).getType());
    }

    public Call listCustomResourceAsTableCall(String pretty) throws ApiException {
        String localVarPath = "/apis/apiextensions.k8s.io/v1/customresourcedefinitions";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{"application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }


}
