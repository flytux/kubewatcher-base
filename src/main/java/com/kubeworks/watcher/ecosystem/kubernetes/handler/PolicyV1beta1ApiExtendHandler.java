package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.RbacV1beta1PodSecurityPolicyTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.PolicyV1beta1Api;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

public class PolicyV1beta1ApiExtendHandler extends PolicyV1beta1Api implements BaseExtendHandler {

    public PolicyV1beta1ApiExtendHandler(ApiClient apiClient) {
        super(apiClient);
    }

    public ApiResponse<RbacV1beta1PodSecurityPolicyTableList> allNamespacePodSecurityPolicyAsTables(String pretty) throws ApiException {
        Call call = listPodSecurityPolicyAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(RbacV1beta1PodSecurityPolicyTableList.class).getType());
    }


    public Call listPodSecurityPolicyAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/policy/v1beta1/podsecuritypolicies";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }
}
