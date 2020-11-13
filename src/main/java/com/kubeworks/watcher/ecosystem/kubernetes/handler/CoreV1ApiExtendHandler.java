package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.*;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1EndpointAddress;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;

import java.util.Collections;
import java.util.List;

@Slf4j
public class CoreV1ApiExtendHandler extends CoreV1Api implements BaseExtendHandler {

    public CoreV1ApiExtendHandler(ApiClient apiClient) {
        super(apiClient);
    }


    public ApiResponse<V1NodeTableList> listNodeAsTable(String pretty) throws ApiException {
        Call call = listNodeAsTableCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1NodeTableList.class).getType());
    }

    public ApiResponse<V1NodeTableList> readNodeAsTable(String nodeName, String pretty) throws ApiException {
        Call call = readNodeAsTableCall(nodeName, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1NodeTableList.class).getType());
    }

    public ApiResponse<V1PodTableList> allNamespacePodAsTable(String pretty, String fieldSelector, String labelSelector) throws ApiException {
        Call call = listPodAsTableAllNamespacesCall(pretty, fieldSelector, labelSelector);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1PodTableList.class).getType());
    }

    public ApiResponse<V1PodTableList> namespacePodAsTable(String namespace, String fieldSelector, String labelSelector, String pretty) throws ApiException {
        Call call = listPodAsTableCall(namespace, fieldSelector, labelSelector, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1PodTableList.class).getType());
    }

    public ApiResponse<V1EventTableList> listAllNamespaceEventAsTable(String pretty, String fieldSelector) throws ApiException {
        Call call = listEventAsTableCall(pretty, fieldSelector);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1EventTableList.class).getType());
    }

    public ApiResponse<V1PersistentVolumeTableList> allPersistentVolumeAsTable(String pretty) throws ApiException {
        Call call = listPersistentVolumeAsTableCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1PersistentVolumeTableList.class).getType());
    }

    public ApiResponse<V1PersistentVolumeClaimTableList> allNamespacePersistentVolumeClaimAsTable(String pretty) throws ApiException {
        Call call = listPersistentVolumeClaimAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1PersistentVolumeClaimTableList.class).getType());
    }

    public ApiResponse<V1ConfigMapTableList> allNamespaceConfigMapAsTable(String pretty) throws ApiException {
        Call call = listConfigMapAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1ConfigMapTableList.class).getType());
    }

    public ApiResponse<V1SecretTableList> allNamespaceSecretAsTable(String pretty) throws ApiException {
        Call call = listSecretAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1SecretTableList.class).getType());
    }

    public ApiResponse<V1ResourceQuotaTableList> allNamespaceResourceQuotaAsTable(String pretty) throws ApiException {
        Call call = listResourceQuotaAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1ResourceQuotaTableList.class).getType());
    }

    public ApiResponse<V1NamespaceTableList> allNamespaceAsTable(String pretty) throws ApiException {
        Call call = listNamespaceAsTableCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1NamespaceTableList.class).getType());
    }

    public ApiResponse<V1LimitRangeTableList> allNamespaceLimitRangeAsTable(String pretty) throws ApiException {
        Call call = listLimitRangeAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1LimitRangeTableList.class).getType());
    }

    public ApiResponse<V1ServiceTableList> allNamespaceServiceAsTables(String pretty) throws ApiException {
        Call call = listServiceAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1ServiceTableList.class).getType());
    }

    public ApiResponse<V1EndpointTableList> allNamespaceEndpointAsTables(String pretty) throws ApiException {
        Call call = listEndpointAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1EndpointTableList.class).getType());
    }

    public ApiResponse<V1EndpointTableList> namespaceEndpointAsTable(String name, String namespace, String pretty) throws ApiException {
        Call call = listEndpointAsTableCall(name, namespace, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1EndpointTableList.class).getType());
    }

    public ApiResponse<V1ServiceAccountTableList> allNamespaceServiceAccountAsTables(String pretty) throws ApiException {
        Call call = listServiceAccountAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1ServiceAccountTableList.class).getType());
    }

    private Call listLimitRangeAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/limitranges";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    private Call listNamespaceAsTableCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }


    private Call listResourceQuotaAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/resourcequotas";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }


    private Call listSecretAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/secrets";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    private Call listConfigMapAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/configmaps";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    private Call listPersistentVolumeAsTableCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/persistentvolumes";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    private Call listPersistentVolumeClaimAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/persistentvolumeclaims";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    private Call listEventAsTableCall(String pretty, String fieldSelector) throws ApiException {
        String localVarPath = "/api/v1/events";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());
        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }
        if (fieldSelector != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("fieldSelector", fieldSelector));
        }
        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    private Call listNodeAsTableCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/nodes";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());
        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }
        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call readNodeAsTableCall(String name, String pretty) throws ApiException {
        String localVarPath = "/api/v1/nodes/{name}".replaceAll("\\{name}", super.getApiClient().escapeString(name));
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());
        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        localVarQueryParams.addAll(super.getApiClient().parameterToPair("exact", true));
        localVarQueryParams.addAll(super.getApiClient().parameterToPair("export", false));

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listPodAsTableAllNamespacesCall(String pretty, String fieldSelector, String labelSelector) throws ApiException {
        String localVarPath = "/api/v1/pods";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (fieldSelector != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("fieldSelector", fieldSelector));
        }
        if (labelSelector != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("labelSelector", labelSelector));
        }

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listPodAsTableCall(String namespace, String fieldSelector, String labelSelector, String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/pods".replaceAll("\\{namespace\\}", super.getApiClient().escapeString(namespace));
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (fieldSelector != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("fieldSelector", fieldSelector));
        }
        if (labelSelector != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("labelSelector", labelSelector));
        }

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listServiceAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/services";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listEndpointAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/endpoints";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    private Call listEndpointAsTableCall(String name, String namespace, String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/endpoints/{name}".replaceAll("\\{namespace\\}", super.getApiClient().escapeString(namespace)).replaceAll("\\{name\\}", super.getApiClient().escapeString(name));
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listServiceAccountAsTableAllNamespacesCall(String pretty) throws ApiException {
        String localVarPath = "/api/v1/serviceaccounts";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }



}
