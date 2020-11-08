package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.RbacV1beta1RoleBindingTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1beta1Api;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

public class RbacV1beta1ApiExtendHandler extends RbacAuthorizationV1beta1Api implements BaseExtendHandler {

    public RbacV1beta1ApiExtendHandler(ApiClient apiClient) {
        super(apiClient);
    }

    public ApiResponse<RbacV1beta1RoleBindingTableList> allNamespaceRoleBindingAsTables(String pretty) throws ApiException {
        Call call = listRoleBindingAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(RbacV1beta1RoleBindingTableList.class).getType());
    }


    public Call listRoleBindingAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/apis/rbac.authorization.k8s.io/v1beta1/rolebindings";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }
}
