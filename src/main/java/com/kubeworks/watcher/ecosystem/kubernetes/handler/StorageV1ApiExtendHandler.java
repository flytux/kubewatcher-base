package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.StorageV1StorageClassTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.StorageV1Api;

import java.lang.reflect.Type;

public class StorageV1ApiExtendHandler extends StorageV1Api implements BaseExtendHandler {

    private static final Type TYPE_STORAGE_V1_STORAGE_CLASS_TABLE_LIST = TypeToken.getParameterized(StorageV1StorageClassTableList.class).getType();

    public StorageV1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<StorageV1StorageClassTableList> searchStorageClassesTableList() {
        return execute("/apis/storage.k8s.io/v1/storageclasses", TYPE_STORAGE_V1_STORAGE_CLASS_TABLE_LIST);
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }
}
