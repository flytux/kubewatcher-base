package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ConfigMapDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ConfigMapTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ConfigMapTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ConfigMapService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ConfigMapServiceImpl implements ConfigMapService {

    private final CoreV1ApiExtendHandler coreV1Api;
    private final EventService eventService;
    private final K8sObjectManager k8sObjectManager;

    @Autowired
    public ConfigMapServiceImpl(ApiClient k8sApiClient, EventService eventService, K8sObjectManager k8sObjectManager) {
        this.coreV1Api = new CoreV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<ConfigMapTable> allNamespaceConfigMapTables() {
        ApiResponse<V1ConfigMapTableList> apiResponse = coreV1Api.searchConfigMapsTableList();
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ConfigMapTableList configMaps = apiResponse.getData();
            List<ConfigMapTable> dataTable = configMaps.createDataTableList();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<ConfigMapTable> configMaps(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceConfigMapTables();
        }
        ApiResponse<V1ConfigMapTableList> apiResponse = coreV1Api.searchConfigMapsTableList(namespace);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ConfigMapTableList configMaps = apiResponse.getData();
            return configMaps.createDataTableList();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<ConfigMapDescribe> configMap(String namespace, String name) {

        ApiResponse<V1ConfigMap> apiResponse = coreV1Api.readNamespacedConfigMapWithHttpInfo(name, namespace, "true", Boolean.TRUE, Boolean.FALSE);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        ConfigMapDescribe.ConfigMapDescribeBuilder builder = ConfigMapDescribe.builder();
        V1ConfigMap data = apiResponse.getData();
        setConfigMap(builder, data);
        ConfigMapDescribe configMapDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("ConfigMap",
            configMapDescribe.getNamespace(), configMapDescribe.getName(), configMapDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> configMapDescribe.setEvents(v1EventTableList.createDataTableList()));

        return Optional.of(configMapDescribe);
    }

    private void setConfigMap(ConfigMapDescribe.ConfigMapDescribeBuilder builder, V1ConfigMap data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getData() != null) {
            Map<String, String> configMapData = data.getData();
            builder.data(configMapData);
        }

        if (data.getBinaryData() != null) {
            Map<String, byte[]> configMapBinaryData = data.getBinaryData();
            List<String> binaryDataKeys = new ArrayList<>(configMapBinaryData.keySet());
            builder.binaryData(binaryDataKeys);
        }
    }
}
