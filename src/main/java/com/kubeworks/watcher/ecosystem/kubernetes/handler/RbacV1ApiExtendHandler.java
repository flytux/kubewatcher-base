package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.RbacV1RoleTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1Api;

import java.lang.reflect.Type;

public class RbacV1ApiExtendHandler extends RbacAuthorizationV1Api implements BaseExtendHandler {

    private static final Type TYPE_RBAC_V1_ROLE_TABLE_LIST = TypeToken.getParameterized(RbacV1RoleTableList.class).getType();

    public RbacV1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<RbacV1RoleTableList> searchRolesTableList() {
        return execute("/apis/rbac.authorization.k8s.io/v1/roles", TYPE_RBAC_V1_ROLE_TABLE_LIST);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }
}
