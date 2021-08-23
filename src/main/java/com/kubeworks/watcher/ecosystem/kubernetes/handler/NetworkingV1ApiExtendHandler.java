package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1NetworkPolicyTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.NetworkingV1Api;

import java.lang.reflect.Type;

public class NetworkingV1ApiExtendHandler extends NetworkingV1Api implements BaseExtendHandler {

    private static final String API_PREFIX = "/apis/networking.k8s.io/v1";
    private static final Type TYPE_NETWORKING_V1_NETWORK_POLICY_TABLE_LIST = TypeToken.getParameterized(NetworkingV1NetworkPolicyTableList.class).getType();

    public NetworkingV1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<NetworkingV1NetworkPolicyTableList> searchNetworkPoliciesTableList() {
        return execute(API_PREFIX + "/networkpolicies", TYPE_NETWORKING_V1_NETWORK_POLICY_TABLE_LIST, Consts.SIMPLE_ACCEPT_PARAMS);
    }

    public ApiResponse<NetworkingV1NetworkPolicyTableList> searchNetworkPoliciesTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/networkpolicies", TYPE_NETWORKING_V1_NETWORK_POLICY_TABLE_LIST, Consts.SIMPLE_ACCEPT_PARAMS);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }
}
