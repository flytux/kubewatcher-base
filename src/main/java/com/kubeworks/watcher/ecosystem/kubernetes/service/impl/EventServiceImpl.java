package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EventDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EventTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1Event;
import io.kubernetes.client.openapi.models.V1EventList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private final CoreV1ApiExtendHandler coreApi;
    private final K8sObjectManager k8sObjectManager;

    @Autowired
    public EventServiceImpl(ApiClient k8sApiClient, K8sObjectManager k8sObjectManager) {
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
        this.k8sObjectManager = k8sObjectManager;
    }

    @Override
    public List<EventTable> allNamespaceEventTables() {
        ApiResponse<V1EventTableList> apiResponse = eventTables(null);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1EventTableList eventTableList = apiResponse.getData();
            List<EventTable> dataTable = eventTableList.createDataTableList();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<EventTable> events(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceEventTables();
        }
        ApiResponse<V1EventTableList> apiResponse = coreApi.searchEventsTableList(namespace, null);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1EventTableList eventTableList = apiResponse.getData();
            return eventTableList.createDataTableList();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<EventDescribe> events() {
        ApiResponse<V1EventList> apiResponse = coreApi.listEventForAllNamespacesWithHttpInfo(null, null, null, null,
            ExternalConstants.DEFAULT_K8S_OBJECT_LIMIT, "false", null, ExternalConstants.DEFAULT_K8S_CLIENT_TIMEOUT_SECONDS, null);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Collections.emptyList();
        }
        V1EventList data = apiResponse.getData();

        return data.getItems().stream()
            .map(v1Event -> {
                EventDescribe.EventDescribeBuilder builder = EventDescribe.builder();
                setEvent(builder, v1Event);
                return builder.build();
            })
            .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public Optional<EventDescribe> event(String namespace, String name) {
        ApiResponse<V1Event> apiResponse = coreApi.readNamespacedEventWithHttpInfo(name, namespace, "true", Boolean.TRUE, Boolean.FALSE);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        EventDescribe.EventDescribeBuilder builder = EventDescribe.builder();
        V1Event data = apiResponse.getData();
        setEvent(builder, data);

        return Optional.of(builder.build());
    }

    @Override
    public Optional<V1EventTableList> eventTable(String kind, String namespace, String name, String uId) {
        String eventFieldSelector = getEventFieldSelector(kind, namespace, name, uId);
        ApiResponse<V1EventTableList> apiResponse = eventTables(eventFieldSelector);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.ofNullable(apiResponse.getData());
        }
        return Optional.empty();
    }

    @SneakyThrows
    private ApiResponse<V1EventTableList> eventTables(String fieldSelector) {
        return coreApi.searchEventsTableList(fieldSelector);
    }

    private void setEvent(EventDescribe.EventDescribeBuilder builder, V1Event data) {
        if (data.getMetadata() == null) {
            return;
        }
        V1ObjectMeta metadata = data.getMetadata();
        builder.name(metadata.getName());
        builder.namespace(metadata.getNamespace());
        builder.uid(metadata.getUid());
        builder.creationTimestamp(metadata.getCreationTimestamp());

        builder.type(data.getType());
        builder.reason(data.getReason());
        builder.message(data.getMessage());

        builder.count(data.getCount() != null ? data.getCount() : 0);
        builder.source(data.getSource());
        builder.firstTimestamp(data.getFirstTimestamp());
        builder.lastTimestamp(data.getLastTimestamp());
        builder.involvedObject(data.getInvolvedObject());

    }

    private static String getEventFieldSelector(String kind, String namespace, String name, String uId) {
        StringJoiner joiner = new StringJoiner(",");

        if (kind != null) {
            joiner.add(ExternalConstants.EVENT_FIELD_SELECTOR_KIND + kind);
        }

        if (name != null) {
            joiner.add(ExternalConstants.EVENT_FIELD_SELECTOR_INVOLVED_OBJECT_NAME_KEY + name);
        }

        if (namespace != null) {
            joiner.add(ExternalConstants.EVENT_FIELD_SELECTOR_INVOLVED_OBJECT_NAMESPACE_KEY + namespace);
        }

        if (uId != null) {
            joiner.add(ExternalConstants.EVENT_FIELD_SELECTOR_INVOLVED_OBJECT_UID_KEY + uId);
        }

        return joiner.toString();
    }
}
