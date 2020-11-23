package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1NetworkPolicyTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

public class NetworkingV1ApiExtendHandler extends NetworkingV1Api implements BaseExtendHandler {

    public NetworkingV1ApiExtendHandler(ApiClient apiClient) {
        super(apiClient);
    }

    public ApiResponse<NetworkingV1NetworkPolicyTableList> allNamespaceNetworkPolicyAsTables(String pretty) throws ApiException {
        Call call = listNetworkPolicyAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(NetworkingV1NetworkPolicyTableList.class).getType());
    }

    public Call listNetworkPolicyAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/networking.k8s.io/v1/networkpolicies";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{"application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }
}
