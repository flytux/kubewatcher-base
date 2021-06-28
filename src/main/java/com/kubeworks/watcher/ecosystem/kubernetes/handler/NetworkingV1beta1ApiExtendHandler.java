package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1beta1IngressTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.NetworkingV1beta1Api;

import java.lang.reflect.Type;

public class NetworkingV1beta1ApiExtendHandler extends NetworkingV1beta1Api implements BaseExtendHandler {

    private static final String API_PREFIX = "/apis/networking.k8s.io/v1beta1";
    private static final Type TYPE_NETWORKING_V1BETA1_INGRESS_TABLE_LIST = TypeToken.getParameterized(NetworkingV1beta1IngressTableList.class).getType();

    public NetworkingV1beta1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<NetworkingV1beta1IngressTableList> searchIngressesTableList() {
        return execute(API_PREFIX + "/ingresses", TYPE_NETWORKING_V1BETA1_INGRESS_TABLE_LIST, Consts.SIMPLE_ACCEPT_PARAMS);
    }

    public ApiResponse<NetworkingV1beta1IngressTableList> searchIngressesTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/ingresses", TYPE_NETWORKING_V1BETA1_INGRESS_TABLE_LIST, Consts.SIMPLE_ACCEPT_PARAMS);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }
}
