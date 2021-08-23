package com.kubeworks.watcher.ecosystem.kubernetes.handler.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.kubeworks.watcher.base.BaseException;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.Pair;
import okhttp3.Call;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface BaseExtendHandler {

    default List<Pair> createQueries(final String pretty, final List<Pair> queries) {

        final ImmutableList.Builder<Pair> builder = ImmutableList.<Pair>builder().addAll(Consts.BASE_PARAMS);

        if (!CollectionUtils.isEmpty(queries)) { builder.addAll(queries); }
        if (Objects.nonNull(pretty)) { builder.add(new Pair("pretty", pretty)); }

        return builder.build();
    }

    default Call createCall(final String path, final List<Pair> queries, final String accepts) throws ApiException {

        final Map<String, String> headerParams = Maps.newHashMap();

        headerParams.put("Accept", accepts);
        headerParams.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        return retrieveApiClient().buildCall(path, "GET", queries, ImmutableList.of(), null, headerParams, ImmutableMap.of(), ImmutableMap.of(), Consts.AUTH_NAMES, null);
    }

    default <T> ApiResponse<T> execute(final String path, final Type type) {
        return execute(path, type, Consts.DEFAULT_ACCEPT_PARAMS, createQueries(Consts.PRETTY_PARAM_VALUE, ImmutableList.of()));
    }

    default <T> ApiResponse<T> execute(final String path, final Type type, final String accepts) {
        return execute(path, type, accepts, createQueries(Consts.PRETTY_PARAM_VALUE, ImmutableList.of()));
    }

    default <T> ApiResponse<T> execute(final String path, final Type type, final List<Pair> queries) {
        return execute(path, type, Consts.DEFAULT_ACCEPT_PARAMS, createQueries(Consts.PRETTY_PARAM_VALUE, queries));
    }

    default <T> ApiResponse<T> execute(final String path, final Type type, final String accepts, final List<Pair> queries) {

        try {
            return retrieveApiClient().execute(createCall(path, queries, accepts), type);
        } catch (final ApiException e) {
            throw new BaseException("Kubernetes call failed !! -> " + e.getMessage(), e);
        }
    }

    default String escape(final String source) {
        return retrieveApiClient().escapeString(source);
    }

    ApiClient retrieveApiClient();

    final class Consts {

        public static final String NAMESPACE_DOUBLE_SLASH_STR = "/namespaces/";

        private static final String PRETTY_PARAM_VALUE = Boolean.TRUE.toString();

        private static final String STREAM_STR = ";stream=watch";
        private static final String TABLE_STR = ";as=Table";
        private static final String GMETA_STR = ";g=meta.k8s.io";

        private static final String JSON_STREAM_PARAM = MediaType.APPLICATION_JSON_VALUE + STREAM_STR;
        private static final String JSON_V1_PARAM = MediaType.APPLICATION_JSON_VALUE + TABLE_STR + ";v=v1" + GMETA_STR;
        private static final String JSON_V1BETA1_PARAM = MediaType.APPLICATION_JSON_VALUE + TABLE_STR + ";v=v1beta1" + GMETA_STR;

        public static final String SIMPLE_ACCEPT_PARAMS = MediaType.APPLICATION_JSON_VALUE + "," + JSON_STREAM_PARAM;
        public static final String DEFAULT_ACCEPT_PARAMS = JSON_V1_PARAM + "," + JSON_V1BETA1_PARAM + "," + MediaType.APPLICATION_JSON_VALUE + "," + JSON_STREAM_PARAM;

        private static final String[] AUTH_NAMES = new String[]{"BearerToken"};

        private static final Pair LIMIT_PAIR = new Pair("limit", String.valueOf(ExternalConstants.DEFAULT_K8S_OBJECT_LIMIT));
        private static final Pair TIMEOUT_SECONDS_PAIR = new Pair("timeoutSeconds", String.valueOf(ExternalConstants.DEFAULT_K8S_CLIENT_TIMEOUT_SECONDS));

        private static final List<Pair> BASE_PARAMS = ImmutableList.of(LIMIT_PAIR, TIMEOUT_SECONDS_PAIR);

        private Consts() {
            throw new UnsupportedOperationException("Cannot instantiate this class");
        }
    }
}
