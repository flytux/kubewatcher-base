package com.kubeworks.watcher.ecosystem.kubernetes.handler.base;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import io.kubernetes.client.openapi.ApiCallback;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Pair;
import okhttp3.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BaseExtendHandler {

    default List<Pair> getDefaultLocalVarQueryParams(ApiClient apiClient) {
        List<Pair> localVarQueryParams = new ArrayList<>();
        localVarQueryParams.addAll(apiClient.parameterToPair("limit", ExternalConstants.DEFAULT_K8S_OBJECT_LIMIT));
        localVarQueryParams.addAll(apiClient.parameterToPair("timeoutSeconds", ExternalConstants.DEFAULT_K8S_CLIENT_TIMEOUT_SECONDS));
        return localVarQueryParams;
    }

    default Call getCall(ApiClient apiClient, String localVarPath, List<Pair> localVarQueryParams,
                         List<Pair> localVarCollectionQueryParams, Object localVarPostBody,
                         String[] localVarAccepts, ApiCallback apiCallback) throws ApiException {
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, String> localVarCookieParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();
        String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        String[] localVarContentTypes = new String[0];
        String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);
        String[] localVarAuthNames = new String[]{"BearerToken"};
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams,
            localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams,
            localVarCookieParams, localVarFormParams, localVarAuthNames, apiCallback);
    }

}
