package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AutoScalingV1HPATableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.AutoscalingV1Api;
import io.kubernetes.client.openapi.apis.AutoscalingV2beta2Api;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

public class AutoscalingV1ApiExtendHandler extends AutoscalingV1Api implements BaseExtendHandler {

    private final AutoscalingV2beta2Api v2beta2Api;

    public AutoscalingV1ApiExtendHandler(ApiClient k8sApiClient) {
        super(k8sApiClient);
        v2beta2Api = new AutoscalingV2beta2Api(k8sApiClient);
    }

    public AutoscalingV2beta2Api getV2beta2Api() {
        return v2beta2Api;
    }

    public ApiResponse<AutoScalingV1HPATableList> allNamespaceHPAAsTable(String pretty) throws ApiException {
        Call call = listHPAAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(AutoScalingV1HPATableList.class).getType());
    }

    public Call listHPAAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/autoscaling/v1/horizontalpodautoscalers";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }
}
