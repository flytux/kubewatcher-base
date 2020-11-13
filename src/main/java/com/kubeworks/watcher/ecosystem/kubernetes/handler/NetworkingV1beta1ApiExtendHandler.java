package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1beta1IngressTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.NetworkingV1beta1Api;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

public class NetworkingV1beta1ApiExtendHandler extends NetworkingV1beta1Api implements BaseExtendHandler {

    public NetworkingV1beta1ApiExtendHandler(ApiClient apiClient) {
        super(apiClient);
    }

    public ApiResponse<NetworkingV1beta1IngressTableList> allNamespaceIngressAsTables(String pretty) throws ApiException {
        Call call = listIngressAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(NetworkingV1beta1IngressTableList.class).getType());
    }


    public Call listIngressAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/networking.k8s.io/v1beta1/ingresses";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{"application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }
}
