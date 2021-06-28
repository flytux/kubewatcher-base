package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.BatchV1JobTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.BatchV1Api;

import java.lang.reflect.Type;

public class BatchV1ApiExtendHandler extends BatchV1Api implements BaseExtendHandler {

    private static final String API_PREFIX = "/apis/batch/v1";
    private static final Type TYPE_BATCH_V1_JOB_TABLE_LIST = TypeToken.getParameterized(BatchV1JobTableList.class).getType();

    public BatchV1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<BatchV1JobTableList> searchBatchJobsTableList() {
        return execute(API_PREFIX + "/jobs", TYPE_BATCH_V1_JOB_TABLE_LIST);
    }

    public ApiResponse<BatchV1JobTableList> searchBatchJobsTableList(String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/jobs", TYPE_BATCH_V1_JOB_TABLE_LIST);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }
}
