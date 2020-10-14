package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.StorageClassDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.StorageClassTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.StorageV1StorageClassTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.StorageV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.StorageService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1StorageClass;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    private final ApiClient k8sApiClient;
    private final StorageV1ApiExtendHandler storageV1ApiExtendHandler;
    private final PodService podService;
    private final EventService eventService;

    public StorageServiceImpl(ApiClient k8sApiClient, PodService podService, EventService eventService) {
        this.k8sApiClient = k8sApiClient;
        this.storageV1ApiExtendHandler = new StorageV1ApiExtendHandler(k8sApiClient);
        this.podService = podService;
        this.eventService = eventService;
    }

    @SneakyThrows
    @Override
    public List<StorageClassTable> allStorageClassClaimTables() {
        ApiResponse<StorageV1StorageClassTableList> apiResponse = storageV1ApiExtendHandler.allNamespaceStorageClassAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            StorageV1StorageClassTableList storageClasses = apiResponse.getData();
            return storageClasses.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<StorageClassDescribe> storageClass(String name) {
        ApiResponse<V1StorageClass> apiResponse = storageV1ApiExtendHandler.readStorageClassWithHttpInfo(name, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        StorageClassDescribe.StorageClassDescribeBuilder builder = StorageClassDescribe.builder();
        V1StorageClass data = apiResponse.getData();
        setStorageClass(builder, data);

        StorageClassDescribe storageClassDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("StorageClass", "",
            storageClassDescribe.getName(), storageClassDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> storageClassDescribe.setEvents(v1EventTableList.getDataTable()));

        return Optional.of(storageClassDescribe);
    }

    private void setStorageClass(StorageClassDescribe.StorageClassDescribeBuilder builder, V1StorageClass data) {
        if (data.getMetadata() == null) {
            return;
        }

        V1ObjectMeta metadata = data.getMetadata();
        builder.name(metadata.getName());
        builder.uid(metadata.getUid());
        builder.labels(metadata.getLabels());
        builder.annotations(metadata.getAnnotations());
        builder.creationTimestamp(metadata.getCreationTimestamp());
        builder.provisioner(data.getProvisioner());
        builder.volumeBindingMode(data.getVolumeBindingMode());
        builder.reclaimPolicy(data.getReclaimPolicy());
    }
}
