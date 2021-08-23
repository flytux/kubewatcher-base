package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AppsV1DaemonSetTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AppsV1DeploymentTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AppsV1StatefulSetTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.AppsV1Api;

import java.lang.reflect.Type;

public class AppsV1ApiExtendHandler extends AppsV1Api implements BaseExtendHandler {

    private static final String API_PREFIX = "/apis/apps/v1";
    private static final Type TYPE_APPS_V1_DEPLOYMENT_TABLE_LIST = TypeToken.getParameterized(AppsV1DeploymentTableList.class).getType();
    private static final Type TYPE_APPS_V1_DAEMON_SETS_TABLE_LIST = TypeToken.getParameterized(AppsV1DaemonSetTableList.class).getType();
    private static final Type TYPE_APPS_V1_STATEFUL_SET_TABLE_LIST = TypeToken.getParameterized(AppsV1StatefulSetTableList.class).getType();

    public AppsV1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<AppsV1DeploymentTableList> searchAppsDeploymentsTableList() {
        return execute(API_PREFIX + "/deployments", TYPE_APPS_V1_DEPLOYMENT_TABLE_LIST);
    }

    public ApiResponse<AppsV1DeploymentTableList> searchAppsDeploymentsTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/deployments", TYPE_APPS_V1_DEPLOYMENT_TABLE_LIST);
    }

    public ApiResponse<AppsV1DaemonSetTableList> searchDaemonSetsTableList() {
        return execute(API_PREFIX + "/daemonsets", TYPE_APPS_V1_DAEMON_SETS_TABLE_LIST);
    }

    public ApiResponse<AppsV1DaemonSetTableList> searchDaemonSetsTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/daemonsets", TYPE_APPS_V1_DAEMON_SETS_TABLE_LIST);
    }

    public ApiResponse<AppsV1StatefulSetTableList> searchStatefulSetsTableList() {
        return execute(API_PREFIX + "/statefulsets", TYPE_APPS_V1_STATEFUL_SET_TABLE_LIST);
    }

    public ApiResponse<AppsV1StatefulSetTableList> searchStatefulSetsTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/statefulsets", TYPE_APPS_V1_STATEFUL_SET_TABLE_LIST);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }
}
