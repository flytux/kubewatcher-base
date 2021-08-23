package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.BatchV1beta1CronJobTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.BatchV1beta1Api;

import java.lang.reflect.Type;

public class BatchV1beta1ApiExtendHandler extends BatchV1beta1Api implements BaseExtendHandler {

    private static final Type TYPE_BATCH_V1BETA1_CRONJOB_TABLE_LIST = TypeToken.getParameterized(BatchV1beta1CronJobTableList.class).getType();

    public BatchV1beta1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<BatchV1beta1CronJobTableList> searchCronJobsTableList() {
        return execute("/apis/batch/v1beta1/cronjobs", TYPE_BATCH_V1BETA1_CRONJOB_TABLE_LIST);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }
}
