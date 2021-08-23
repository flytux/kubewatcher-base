package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AutoScalingV1HPATableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.AutoscalingV1Api;
import io.kubernetes.client.openapi.apis.AutoscalingV2beta2Api;
import lombok.Getter;

import java.lang.reflect.Type;

@Getter
public class AutoscalingV1ApiExtendHandler extends AutoscalingV1Api implements BaseExtendHandler {

    private static final String API_PREFIX = "/apis/autoscaling/v1";
    private static final Type TYPE_AUTO_SCALING_V1_HPA_TABLE_LIST = TypeToken.getParameterized(AutoScalingV1HPATableList.class).getType();

    private final AutoscalingV2beta2Api v2beta2Api;

    public AutoscalingV1ApiExtendHandler(final ApiClient client) {
        super(client); this.v2beta2Api = new AutoscalingV2beta2Api(client);
    }

    public ApiResponse<AutoScalingV1HPATableList> searchHorizontalPodAutoScalersTableList() {
        return execute(API_PREFIX + "/horizontalpodautoscalers", TYPE_AUTO_SCALING_V1_HPA_TABLE_LIST);
    }

    public ApiResponse<AutoScalingV1HPATableList> searchHorizontalPodAutoScalersTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/horizontalpodautoscalers", TYPE_AUTO_SCALING_V1_HPA_TABLE_LIST);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }
}
