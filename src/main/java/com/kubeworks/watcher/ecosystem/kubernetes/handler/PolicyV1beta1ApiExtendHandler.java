package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.RbacV1beta1PodSecurityPolicyTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.PolicyV1beta1Api;

import java.lang.reflect.Type;

public class PolicyV1beta1ApiExtendHandler extends PolicyV1beta1Api implements BaseExtendHandler {

    private static final Type TYPE_RBAC_V1BETA1_POD_SECURITY_POLICY_TABLE_LIST = TypeToken.getParameterized(RbacV1beta1PodSecurityPolicyTableList.class).getType();

    public PolicyV1beta1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<RbacV1beta1PodSecurityPolicyTableList> searchPodSecurityPoliciesTableList() {
        return execute("/apis/policy/v1beta1/podsecuritypolicies", TYPE_RBAC_V1BETA1_POD_SECURITY_POLICY_TABLE_LIST);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }
}
