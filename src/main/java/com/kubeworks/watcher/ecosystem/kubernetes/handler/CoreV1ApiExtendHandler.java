package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.*;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.*;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;

import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class CoreV1ApiExtendHandler extends CoreV1Api implements BaseExtendHandler {

    public CoreV1ApiExtendHandler(ApiClient apiClient) {
        super(apiClient);
    }


    public ApiResponse<V1NodeTableList> listNodeAsTable(String pretty) throws ApiException {
        Call call = listNodeAsTableCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1NodeTableList.class).getType());
    }

    public ApiResponse<V1NodeMetricTableList> listMetricNodeAsTable(String pretty) throws ApiException {
        Call call = listMetricNodeAsTableCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1NodeMetricTableList.class).getType());
    }

    public ApiResponse<NodeMetrics> metricNode(String name, String pretty) throws ApiException {
        Call call = listNodeMetricCall(name, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(NodeMetrics.class).getType());
    }

    public ApiResponse<V1NodeTableList> readNodeAsTable(String nodeName, String pretty) throws ApiException {
        Call call = readNodeAsTableCall(nodeName, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1NodeTableList.class).getType());
    }

    public ApiResponse<V1PodTableList> allNamespacePodAsTable(String pretty, String fieldSelector, String labelSelector) throws ApiException {
        Call call = listPodAsTableAllNamespacesCall(pretty, fieldSelector, labelSelector);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1PodTableList.class).getType());
    }

    public ApiResponse<V1PodMetricTableList> metricPodAsTable(String pretty, String fieldSelector, String labelSelector) throws ApiException {
        Call call = listPodMetricAsTableCall(pretty, fieldSelector, labelSelector);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1PodMetricTableList.class).getType());
    }

    public ApiResponse<V1PodTableList> namespacePodAsTable(String namespace, String fieldSelector, String labelSelector, String pretty) throws ApiException {
        Call call = listPodAsTableCall(namespace, fieldSelector, labelSelector, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1PodTableList.class).getType());
    }

    public ApiResponse<V1PodMetricTableList> namespacePodMetricAsTable(String namespace, String fieldSelector, String labelSelector, String pretty) throws ApiException {
        Call call = listPodMetricAsNamespaceCall(namespace, fieldSelector, labelSelector, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1PodMetricTableList.class).getType());
    }

    public ApiResponse<V1EventTableList> listAllNamespaceEventAsTable(String pretty, String fieldSelector) throws ApiException {
        Call call = listEventAsTableCall(pretty, fieldSelector);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1EventTableList.class).getType());
    }

    public ApiResponse<V1EventTableList> listNamespaceEventAsTable(String namespace, String pretty, String fieldSelector) throws ApiException {
        Call call = listEventAsTableNamespacesCall(namespace, pretty, fieldSelector);
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

    public ApiResponse<V1PersistentVolumeClaimTableList> namespacePersistentVolumeClaimAsTable(String namespace, String pretty) throws ApiException {
        Call call = listPersistentVolumeClaimAsTableNamespacesCall(namespace, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1PersistentVolumeClaimTableList.class).getType());
    }

    public ApiResponse<V1ConfigMapTableList> allNamespaceConfigMapAsTable(String pretty) throws ApiException {
        Call call = listConfigMapAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1ConfigMapTableList.class).getType());
    }

    public ApiResponse<V1ConfigMapTableList> namespaceConfigMapAsTable(String namespace, String pretty) throws ApiException {
        Call call = listConfigMapAsTableNamespacesCall(namespace, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1ConfigMapTableList.class).getType());
    }

    public ApiResponse<V1SecretTableList> allNamespaceSecretAsTable(String pretty) throws ApiException {
        Call call = listSecretAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1SecretTableList.class).getType());
    }

    public ApiResponse<V1SecretTableList> namespaceSecretAsTable(String namespace, String pretty) throws ApiException {
        Call call = listSecretAsTableNamespacesCall(namespace, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1SecretTableList.class).getType());
    }

    public ApiResponse<V1ResourceQuotaTableList> allNamespaceResourceQuotaAsTable(String pretty) throws ApiException {
        Call call = listResourceQuotaAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1ResourceQuotaTableList.class).getType());
    }

    public ApiResponse<V1ResourceQuotaTableList> namespaceResourceQuotaAsTable(String namespace, String pretty) throws ApiException {
        Call call = listResourceQuotaAsTableNamespacesCall(namespace, pretty);
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

    public ApiResponse<V1ServiceTableList> namespaceServiceAsTables(String namespace, String pretty) throws ApiException {
        Call call = listServiceAsTableNamespacesCall(namespace, pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1ServiceTableList.class).getType());
    }

    public ApiResponse<V1EndpointTableList> allNamespaceEndpointAsTables(String pretty) throws ApiException {
        Call call = listEndpointAsTableAllNamespacesCall(pretty);
        return super.getApiClient().execute(call, TypeToken.getParameterized(V1EndpointTableList.class).getType());
    }

    public ApiResponse<V1EndpointTableList> namespaceEndpointAsTables(String namespace, String pretty) throws ApiException {
        Call call = listEndpointAsTableNamespacesCall(namespace, pretty);
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

    public ApiResponse<V1ServiceAccountTableList> namespaceServiceAccountAsTables(String namespace, String pretty) throws ApiException {
        Call call = listServiceAccountAsTableNamespacesCall(namespace, pretty);
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

    private Call listResourceQuotaAsTableNamespacesCall(String namespace, String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/resourcequotas".replaceAll("\\{" + "namespace" + "}", namespace);
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

    private Call listSecretAsTableNamespacesCall(String namespace, String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/secrets".replaceAll("\\{" + "namespace" + "}", namespace);
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

    private Call listConfigMapAsTableNamespacesCall(String namespace, String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/configmaps".replaceAll("\\{" + "namespace" + "}", namespace);
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

    private Call listPersistentVolumeClaimAsTableNamespacesCall(String namespace, String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/persistentvolumeclaims".replaceAll("\\{" + "namespace" + "}", namespace);
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

    private Call listEventAsTableNamespacesCall(String namespace, String pretty, String fieldSelector) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/events".replaceAll("\\{" + "namespace" + "}", namespace);
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

    private Call listMetricNodeAsTableCall(String pretty) throws ApiException {
        String localVarPath = "/apis/metrics.k8s.io/v1beta1/nodes";
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());
        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }
        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call listNodeMetricCall(String name, String pretty) throws ApiException {
        String localVarPath = "/apis/metrics.k8s.io/v1beta1/nodes/{name}".replaceAll("\\{name}", super.getApiClient().escapeString(name));
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());
        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        localVarQueryParams.addAll(super.getApiClient().parameterToPair("exact", true));
        localVarQueryParams.addAll(super.getApiClient().parameterToPair("export", false));

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

    public Call listPodMetricAsTableCall(String pretty, String fieldSelector, String labelSelector) throws ApiException {
        String localVarPath = "/apis/metrics.k8s.io/v1beta1/pods";
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
        String localVarPath = "/api/v1/namespaces/{namespace}/pods".replaceAll("\\{namespace}", super.getApiClient().escapeString(namespace));
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

    public Call listPodMetricAsNamespaceCall(String namespace, String fieldSelector, String labelSelector, String pretty) throws ApiException {
        String localVarPath = "/apis/metrics.k8s.io/v1beta1/namespaces/{namespace}/pods".replaceAll("\\{namespace}", super.getApiClient().escapeString(namespace));
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

    public Call listServiceAsTableNamespacesCall(String namespace, String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/services".replaceAll("\\{" + "namespace" + "}", namespace);
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

    public Call listEndpointAsTableNamespacesCall(String namespace, String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/endpoints".replaceAll("\\{" + "namespace" + "}", namespace);
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

    public Call listServiceAccountAsTableNamespacesCall(String namespace, String pretty) throws ApiException {
        String localVarPath = "/api/v1/namespaces/{namespace}/serviceaccounts".replaceAll("\\{" + "namespace" + "}", namespace);
        List<Pair> localVarQueryParams = getDefaultLocalVarQueryParams(super.getApiClient());

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        String[] localVarAccepts = new String[]{ExternalConstants.REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE, "application/json", "application/yaml", "application/vnd.kubernetes.protobuf", "application/json;stream=watch", "application/vnd.kubernetes.protobuf;stream=watch"};
        return getCall(super.getApiClient(), localVarPath, localVarQueryParams, Collections.emptyList(), null, localVarAccepts, null);
    }

    public Call readNamespacedPodLogCall(String name, String namespace, String container, Boolean follow, Boolean insecureSkipTLSVerifyBackend, Integer limitBytes, String pretty, Boolean previous, Integer sinceSeconds, String sinceTime, Integer tailLines, Boolean timestamps, ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;
        String localVarPath = "/api/v1/namespaces/{namespace}/pods/{name}/log".replaceAll("\\{name\\}", super.getApiClient().escapeString(name.toString())).replaceAll("\\{namespace\\}", super.getApiClient().escapeString(namespace.toString()));
        List<Pair> localVarQueryParams = new ArrayList();
        List<Pair> localVarCollectionQueryParams = new ArrayList();
        if (container != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("container", container));
        }

        if (follow != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("follow", follow));
        }

        if (insecureSkipTLSVerifyBackend != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("insecureSkipTLSVerifyBackend", insecureSkipTLSVerifyBackend));
        }

        if (limitBytes != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("limitBytes", limitBytes));
        }

        if (pretty != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("pretty", pretty));
        }

        if (previous != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("previous", previous));
        }

        if (sinceTime != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("sinceSeconds", sinceSeconds));
        }

        if (sinceTime != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("sinceTime", sinceTime));
        }

        if (tailLines != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("tailLines", tailLines));
        }

        if (timestamps != null) {
            localVarQueryParams.addAll(super.getApiClient().parameterToPair("timestamps", timestamps));
        }

        Map<String, String> localVarHeaderParams = new HashMap();
        Map<String, String> localVarCookieParams = new HashMap();
        Map<String, Object> localVarFormParams = new HashMap();
        String[] localVarAccepts = new String[]{"text/plain", "application/json", "application/yaml", "application/vnd.kubernetes.protobuf"};
        String localVarAccept = super.getApiClient().selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        String[] localVarContentTypes = new String[0];
        String localVarContentType = super.getApiClient().selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);
        String[] localVarAuthNames = new String[]{"BearerToken"};
        return super.getApiClient().buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    private Call readNamespacedPodLogValidateBeforeCall(String name, String namespace, String container, Boolean follow, Boolean insecureSkipTLSVerifyBackend, Integer limitBytes, String pretty, Boolean previous, Integer sinceSeconds, String sinceTime, Integer tailLines, Boolean timestamps, ApiCallback _callback) throws ApiException {
        if (name == null) {
            throw new ApiException("Missing the required parameter 'name' when calling readNamespacedPodLog(Async)");
        } else if (namespace == null) {
            throw new ApiException("Missing the required parameter 'namespace' when calling readNamespacedPodLog(Async)");
        } else {
            Call localVarCall = this.readNamespacedPodLogCall(name, namespace, container, follow, insecureSkipTLSVerifyBackend, limitBytes, pretty, previous, sinceSeconds, sinceTime, tailLines, timestamps, _callback);
            return localVarCall;
        }
    }

    public String readNamespacedPodLog(String name, String namespace, String container, Boolean follow, Boolean insecureSkipTLSVerifyBackend, Integer limitBytes, String pretty, Boolean previous, Integer sinceSeconds, String sinceTime, Integer tailLines, Boolean timestamps) throws ApiException {
        ApiResponse<String> localVarResp = this.readNamespacedPodLogWithHttpInfo(name, namespace, container, follow, insecureSkipTLSVerifyBackend, limitBytes, pretty, previous, sinceSeconds, sinceTime, tailLines, timestamps);
        return (String)localVarResp.getData();
    }

    public ApiResponse<String> readNamespacedPodLogWithHttpInfo(String name, String namespace, String container, Boolean follow, Boolean insecureSkipTLSVerifyBackend, Integer limitBytes, String pretty, Boolean previous, Integer sinceSeconds, String sinceTime, Integer tailLines, Boolean timestamps) throws ApiException {
        Call localVarCall = this.readNamespacedPodLogValidateBeforeCall(name, namespace, container, follow, insecureSkipTLSVerifyBackend, limitBytes, pretty, previous, sinceSeconds, sinceTime, tailLines, timestamps, (ApiCallback)null);
        Type localVarReturnType = (new TypeToken<String>() {
        }).getType();
        return super.getApiClient().execute(localVarCall, localVarReturnType);
    }
}
