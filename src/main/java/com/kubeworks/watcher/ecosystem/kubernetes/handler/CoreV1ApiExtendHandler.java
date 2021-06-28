package com.kubeworks.watcher.ecosystem.kubernetes.handler;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import com.kubeworks.watcher.base.BaseException;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.*;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.base.BaseExtendHandler;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import lombok.Builder;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class CoreV1ApiExtendHandler extends CoreV1Api implements BaseExtendHandler {

    private static final String API_PREFIX = "/api/v1";
    private static final String API_PREFIX_BETA = "/apis/metrics.k8s.io/v1beta1";

    private static final String PODS_SLASH_STR = "/pods";
    private static final String NODES_SLASH_STR = "/nodes";

    private static final Type TYPE_V1_NODE_TABLE_LIST;
    private static final Type TYPE_V1_NODE_METRIC_TABLE_LIST;
    private static final Type TYPE_NODE_METRICS;
    private static final Type TYPE_V1_POD_TABLE_LIST;
    private static final Type TYPE_V1_POD_METRIC_TABLE_LIST;
    private static final Type TYPE_V1_EVENT_TABLE_LIST;
    private static final Type TYPE_V1_PERSISTENT_VOLUME_TABLE_LIST;
    private static final Type TYPE_V1_PERSISTENT_VOLUME_CLAIM_TABLE_LIST;
    private static final Type TYPE_V1_CONFIG_MAP_TABLE_LIST;
    private static final Type TYPE_V1_SECRET_TABLE_LIST;
    private static final Type TYPE_V1_RESOURCE_QUOTA_TABLE_LIST;
    private static final Type TYPE_V1_NAMESPACE_TABLE_LIST;
    private static final Type TYPE_V1_LIMIT_RANGE_TABLE_LIST;
    private static final Type TYPE_V1_SERVICE_TABLE_LIST;
    private static final Type TYPE_V1_ENDPOINT_TABLE_LIST;
    private static final Type TYPE_V1_SERVICE_ACCOUNT_TABLE_LIST;
    private static final Type TYPE_STRING;

    private static final List<Pair> NODE_EXTRA_PARAMS;

    private static final Pair FOLLOW_PAIR;
    private static final Pair INSECURE_PAIR;
    private static final Pair PREVIOUS_PAIR;
    private static final Pair TIMESTAMPS_PAIR;

    static {
        TYPE_V1_NODE_TABLE_LIST = TypeToken.getParameterized(V1NodeTableList.class).getType();
        TYPE_V1_NODE_METRIC_TABLE_LIST = TypeToken.getParameterized(V1NodeMetricTableList.class).getType();
        TYPE_NODE_METRICS = TypeToken.getParameterized(NodeMetrics.class).getType();
        TYPE_V1_POD_TABLE_LIST = TypeToken.getParameterized(V1PodTableList.class).getType();
        TYPE_V1_POD_METRIC_TABLE_LIST = TypeToken.getParameterized(V1PodMetricTableList.class).getType();
        TYPE_V1_EVENT_TABLE_LIST = TypeToken.getParameterized(V1EventTableList.class).getType();
        TYPE_V1_PERSISTENT_VOLUME_TABLE_LIST = TypeToken.getParameterized(V1PersistentVolumeTableList.class).getType();
        TYPE_V1_PERSISTENT_VOLUME_CLAIM_TABLE_LIST = TypeToken.getParameterized(V1PersistentVolumeClaimTableList.class).getType();
        TYPE_V1_CONFIG_MAP_TABLE_LIST = TypeToken.getParameterized(V1ConfigMapTableList.class).getType();
        TYPE_V1_SECRET_TABLE_LIST = TypeToken.getParameterized(V1SecretTableList.class).getType();
        TYPE_V1_RESOURCE_QUOTA_TABLE_LIST = TypeToken.getParameterized(V1ResourceQuotaTableList.class).getType();
        TYPE_V1_NAMESPACE_TABLE_LIST = TypeToken.getParameterized(V1NamespaceTableList.class).getType();
        TYPE_V1_LIMIT_RANGE_TABLE_LIST = TypeToken.getParameterized(V1LimitRangeTableList.class).getType();
        TYPE_V1_SERVICE_TABLE_LIST = TypeToken.getParameterized(V1ServiceTableList.class).getType();
        TYPE_V1_ENDPOINT_TABLE_LIST = TypeToken.getParameterized(V1EndpointTableList.class).getType();
        TYPE_V1_SERVICE_ACCOUNT_TABLE_LIST = TypeToken.getParameterized(V1ServiceAccountTableList.class).getType();
        TYPE_STRING = new TypeToken<String>(){}.getType();

        NODE_EXTRA_PARAMS = ImmutableList.of(new Pair("exact", String.valueOf(Boolean.TRUE)), new Pair("export", String.valueOf(Boolean.FALSE)));

        FOLLOW_PAIR = new Pair("follow", String.valueOf(Boolean.FALSE));
        INSECURE_PAIR = new Pair("insecureSkipTLSVerifyBackend", String.valueOf(Boolean.FALSE));
        PREVIOUS_PAIR = new Pair("previous", String.valueOf(Boolean.FALSE));
        TIMESTAMPS_PAIR = new Pair("timestamps", String.valueOf(Boolean.TRUE));
    }

    public CoreV1ApiExtendHandler(final ApiClient client) {
        super(client);
    }

    public ApiResponse<V1NodeTableList> searchNodesTableList() {
        return execute(API_PREFIX + NODES_SLASH_STR, TYPE_V1_NODE_TABLE_LIST);
    }

    public ApiResponse<V1NodeTableList> searchNodesTableList(final String name) {
        return execute(API_PREFIX + NODES_SLASH_STR + "/" + escape(name), TYPE_V1_NODE_TABLE_LIST, NODE_EXTRA_PARAMS);
    }

    public ApiResponse<V1NodeMetricTableList> searchNodeMetricTableList() {
        return execute(API_PREFIX_BETA + NODES_SLASH_STR, TYPE_V1_NODE_METRIC_TABLE_LIST);
    }

    public ApiResponse<NodeMetrics> searchNodeMetric(final String name) {
        return execute(API_PREFIX_BETA + NODES_SLASH_STR + "/" + escape(name), TYPE_NODE_METRICS, NODE_EXTRA_PARAMS);
    }

    public ApiResponse<V1PodTableList> searchPodsTableList(final String fs, final String ls) {
        return execute(API_PREFIX + PODS_SLASH_STR, TYPE_V1_POD_TABLE_LIST, createFieldLabelSelectorParams(fs, ls));
    }

    public ApiResponse<V1PodTableList> searchPodsTableList(final String namespace, final String fs, final String ls) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + PODS_SLASH_STR, TYPE_V1_POD_TABLE_LIST, createFieldLabelSelectorParams(fs, ls));
    }

    public ApiResponse<V1PodMetricTableList> searchPodMetricTableList(final String fs, final String ls) {
        return execute(API_PREFIX_BETA + PODS_SLASH_STR, TYPE_V1_POD_METRIC_TABLE_LIST, createFieldLabelSelectorParams(fs, ls));
    }

    public ApiResponse<V1PodMetricTableList> searchPodMetricTableList(final String namespace, final String fs, final String ls) {
        return execute(API_PREFIX_BETA + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + PODS_SLASH_STR, TYPE_V1_POD_METRIC_TABLE_LIST, createFieldLabelSelectorParams(fs, ls));
    }

    public ApiResponse<V1EventTableList> searchEventsTableList(final String fs) {
        return execute(API_PREFIX + "/events", TYPE_V1_EVENT_TABLE_LIST, createFieldSelectorParams(fs).build());
    }

    public ApiResponse<V1EventTableList> searchEventsTableList(final String namespace, final String fs) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/events", TYPE_V1_EVENT_TABLE_LIST, createFieldSelectorParams(fs).build());
    }

    public ApiResponse<V1PersistentVolumeTableList> searchPersistentVolumesTableList() {
        return execute(API_PREFIX + "/persistentvolumes", TYPE_V1_PERSISTENT_VOLUME_TABLE_LIST);
    }

    public ApiResponse<V1PersistentVolumeClaimTableList> searchPersistentVolumeClaimsTableList() {
        return execute(API_PREFIX + "/persistentvolumeclaims", TYPE_V1_PERSISTENT_VOLUME_CLAIM_TABLE_LIST);
    }

    public ApiResponse<V1PersistentVolumeClaimTableList> searchPersistentVolumeClaimsTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/persistentvolumeclaims", TYPE_V1_PERSISTENT_VOLUME_CLAIM_TABLE_LIST);
    }

    public ApiResponse<V1ConfigMapTableList> searchConfigMapsTableList() {
        return execute(API_PREFIX + "/configmaps", TYPE_V1_CONFIG_MAP_TABLE_LIST);
    }

    public ApiResponse<V1ConfigMapTableList> searchConfigMapsTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/configmaps", TYPE_V1_CONFIG_MAP_TABLE_LIST);
    }

    public ApiResponse<V1SecretTableList> searchSecretsTableList() {
        return execute(API_PREFIX + "/secrets", TYPE_V1_SECRET_TABLE_LIST);
    }

    public ApiResponse<V1SecretTableList> searchSecretsTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/secrets", TYPE_V1_SECRET_TABLE_LIST);
    }

    public ApiResponse<V1ResourceQuotaTableList> searchResourceQuotasTableList() {
        return execute(API_PREFIX + "/resourcequotas", TYPE_V1_RESOURCE_QUOTA_TABLE_LIST);
    }

    public ApiResponse<V1ResourceQuotaTableList> searchResourceQuotasTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/resourcequotas", TYPE_V1_RESOURCE_QUOTA_TABLE_LIST);
    }

    public ApiResponse<V1NamespaceTableList> searchNamespacesTableList() {
        return execute(API_PREFIX + "/namespaces", TYPE_V1_NAMESPACE_TABLE_LIST);
    }

    public ApiResponse<V1LimitRangeTableList> searchLimitRangesTableList() {
        return execute(API_PREFIX + "/limitranges", TYPE_V1_LIMIT_RANGE_TABLE_LIST);
    }

    public ApiResponse<V1ServiceTableList> searchServicesTableList() {
        return execute(API_PREFIX + "/services", TYPE_V1_SERVICE_TABLE_LIST);
    }

    public ApiResponse<V1ServiceTableList> searchServicesTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/services", TYPE_V1_SERVICE_TABLE_LIST);
    }

    public ApiResponse<V1EndpointTableList> searchEndpointsTableList() {
        return execute(API_PREFIX + "/endpoints", TYPE_V1_ENDPOINT_TABLE_LIST);
    }

    public ApiResponse<V1EndpointTableList> searchEndpointsTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/endpoints", TYPE_V1_ENDPOINT_TABLE_LIST);
    }

    public ApiResponse<V1EndpointTableList> searchEndpointsTableList(final String namespace, final String name) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/endpoints/" + escape(name), TYPE_V1_ENDPOINT_TABLE_LIST);
    }

    public ApiResponse<V1ServiceAccountTableList> searchServiceAccountsTableList() {
        return execute(API_PREFIX + "/serviceaccounts", TYPE_V1_SERVICE_ACCOUNT_TABLE_LIST);
    }

    public ApiResponse<V1ServiceAccountTableList> searchServiceAccountsTableList(final String namespace) {
        return execute(API_PREFIX + Consts.NAMESPACE_DOUBLE_SLASH_STR + escape(namespace) + "/serviceaccounts", TYPE_V1_SERVICE_ACCOUNT_TABLE_LIST);
    }

    public String searchPodsLog(final String namespace, final String name, final PodLogParam param) {
        return searchPodsLogAsString(namespace, name, param).getData();
    }

    @Override
    public ApiClient retrieveApiClient() {
        return super.getApiClient();
    }

    private ImmutableList.Builder<Pair> createFieldSelectorParams(final String fs) {

        final ImmutableList.Builder<Pair> builder = ImmutableList.builder();

        if (StringUtils.hasText(fs)) { builder.add(new Pair("fieldSelector", fs)); }

        return builder;
    }

    private List<Pair> createFieldLabelSelectorParams(final String fs, final String ls) {

        final ImmutableList.Builder<Pair> builder = createFieldSelectorParams(fs);

        if (StringUtils.hasText(ls)) { builder.add(new Pair("labelSelector", ls)); }

        return builder.build();
    }

    private List<Pair> createPodsLogParams(final ImmutableList.Builder<Pair> builder) {
        return builder.add(FOLLOW_PAIR).add(INSECURE_PAIR).add(PREVIOUS_PAIR).add(TIMESTAMPS_PAIR).build();
    }

    private ApiResponse<String> searchPodsLogAsString(final String namespace, final String name, final PodLogParam param) {

        if (Objects.isNull(namespace)) {
            throw new BaseException("Missing the required parameter 'namespace' when calling readNamespacedPodLog(Async)");
        }
        if (Objects.isNull(name)) {
            throw new BaseException("Missing the required parameter 'name' when calling readNamespacedPodLog(Async)");
        }

        return execute(API_PREFIX + "/namespaces/" + escape(namespace) + "/pods/" + escape(name) + "/log", TYPE_STRING, MediaType.APPLICATION_JSON_VALUE, createPodsLogParams(param.createParamsIfAvailable()));
    }

    public static final class PodLogParam {

        private final String container;
        private final String sinceTime;
        private final Integer tailLines;

        @Builder
        private PodLogParam(final String container, final String sinceTime, final Integer tailLines) {
            this.container = container; this.sinceTime = sinceTime; this.tailLines = tailLines;
        }

        public ImmutableList.Builder<Pair> createParamsIfAvailable() {

            final ImmutableList.Builder<Pair> builder = ImmutableList.builder();

            if (Objects.nonNull(container)) { builder.add(new Pair("container", container)); }
            if (Objects.nonNull(sinceTime)) { builder.add(new Pair("sinceTime", sinceTime)); }
            if (Objects.nonNull(tailLines)) { builder.add(new Pair("tailLines", String.valueOf(tailLines))); }

            return builder;
        }
    }
}
