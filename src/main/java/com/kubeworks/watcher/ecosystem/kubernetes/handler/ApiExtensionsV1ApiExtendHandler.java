package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.ApiExtV1CustomResourceTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.ApiextensionsV1Api;

import java.lang.reflect.Type;

public class ApiExtensionsV1ApiExtendHandler extends ApiextensionsV1Api implements BaseExtendHandler {

    private static final Type TYPE_API_EXT_V1_CUSTOM_RESOURCE_TABLE_LIST = TypeToken.getParameterized(ApiExtV1CustomResourceTableList.class).getType();

    public ApiExtensionsV1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<ApiExtV1CustomResourceTableList> searchCustomResourceDefinitionsList() {
        return execute("/apis/apiextensions.k8s.io/v1/customresourcedefinitions", TYPE_API_EXT_V1_CUSTOM_RESOURCE_TABLE_LIST, Consts.SIMPLE_ACCEPT_PARAMS);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return getApiClient();
    }
}
