package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1PersistentVolumeClaimTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1PersistentVolumeTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PersistentVolumeService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodService;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class PersistentVolumeServiceImpl implements PersistentVolumeService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreV1ApiExtendHandler;
    private final PodService podService;
    private final EventService eventService;

    public PersistentVolumeServiceImpl(ApiClient k8sApiClient, PodService podService, EventService eventService) {
        this.k8sApiClient = k8sApiClient;
        this.coreV1ApiExtendHandler = new CoreV1ApiExtendHandler(k8sApiClient);
        this.podService = podService;
        this.eventService = eventService;
    }

    @SneakyThrows
    @Override
    public List<PersistentVolumeTable> allPersistentVolumeTables() {
        ApiResponse<V1PersistentVolumeTableList> apiResponse = coreV1ApiExtendHandler.allPersistentVolumeAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1PersistentVolumeTableList persistentVolumes = apiResponse.getData();
            return persistentVolumes.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<PersistentVolumeDescribe> persistentVolume(String name) {
        ApiResponse<V1PersistentVolume> apiResponse = coreV1ApiExtendHandler.readPersistentVolumeWithHttpInfo(name, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        PersistentVolumeDescribe.PersistentVolumeDescribeBuilder builder = PersistentVolumeDescribe.builder();
        V1PersistentVolume data = apiResponse.getData();
        setPersistentVolume(builder, data);

        PersistentVolumeDescribe persistentVolumeDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("PersistentVolume",
            "", persistentVolumeDescribe.getName(), persistentVolumeDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> persistentVolumeDescribe.setEvents(v1EventTableList.getDataTable()));

        return Optional.of(persistentVolumeDescribe);
    }

    @SneakyThrows
    @Override
    public List<PersistentVolumeClaimTable> allNamespacePersistentVolumeClaimTables() {
        ApiResponse<V1PersistentVolumeClaimTableList> apiResponse = coreV1ApiExtendHandler.allNamespacePersistentVolumeClaimAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1PersistentVolumeClaimTableList persistentVolumeClaims = apiResponse.getData();
            return persistentVolumeClaims.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<PersistentVolumeClaimDescribe> persistentVolumeClaim(String namespace, String name) {
        ApiResponse<V1PersistentVolumeClaim> apiResponse = coreV1ApiExtendHandler
            .readNamespacedPersistentVolumeClaimWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        PersistentVolumeClaimDescribe.PersistentVolumeClaimDescribeBuilder builder = PersistentVolumeClaimDescribe.builder();
        V1PersistentVolumeClaim data = apiResponse.getData();
        setPersistentVolumeClaim(builder, data);
        PersistentVolumeClaimDescribe persistentVolumeClaimDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("PersistentVolumeClaim",
            persistentVolumeClaimDescribe.getNamespace(),
            persistentVolumeClaimDescribe.getName(),
            persistentVolumeClaimDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> persistentVolumeClaimDescribe.setEvents(v1EventTableList.getDataTable()));

        return Optional.of(persistentVolumeClaimDescribe);
    }

    private void setPersistentVolumeClaim(PersistentVolumeClaimDescribe.PersistentVolumeClaimDescribeBuilder builder, V1PersistentVolumeClaim data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
            builder.finalizers(metadata.getFinalizers());
        }

        if (data.getSpec() != null) {
            V1PersistentVolumeClaimSpec spec = data.getSpec();
            builder.accessModes(spec.getAccessModes());
            builder.storageClassName(spec.getStorageClassName());
            setStorageResource(builder, spec);
            builder.selector(spec.getSelector());
        }

        if (data.getStatus() != null) {
            V1PersistentVolumeClaimStatus status = data.getStatus();
            builder.status(status.getPhase());
            builder.capacity(status.getCapacity());
        }
    }

    private void setStorageResource(PersistentVolumeClaimDescribe.PersistentVolumeClaimDescribeBuilder builder, V1PersistentVolumeClaimSpec spec) {
        V1ResourceRequirements resources = spec.getResources();
        if (resources == null) {
            return;
        }

        if (MapUtils.isNotEmpty(resources.getLimits())
            || MapUtils.isNotEmpty(resources.getRequests())) {
            ObjectUsageResource objectUsageResource = new ObjectUsageResource();
            objectUsageResource.setLimits(resources.getLimits());
            objectUsageResource.setRequests(resources.getRequests());
            builder.resources(objectUsageResource);
        }
    }

    private void setPersistentVolume(PersistentVolumeDescribe.PersistentVolumeDescribeBuilder builder, V1PersistentVolume data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());

            builder.finalizers(metadata.getFinalizers());
        }

        if (data.getSpec() != null) {
            V1PersistentVolumeSpec spec = data.getSpec();
            builder.accessModes(spec.getAccessModes());
            builder.reclaimPolicy(spec.getPersistentVolumeReclaimPolicy());
            builder.storageClassName(spec.getStorageClassName());
            V1ObjectReference claimRef = spec.getClaimRef();
            if (claimRef != null) {
                PersistentVolumeClaimTable persistentVolumeClaimTable = new PersistentVolumeClaimTable();
                persistentVolumeClaimTable.setName(claimRef.getName());
                persistentVolumeClaimTable.setNamespace(claimRef.getNamespace());
                builder.claim(persistentVolumeClaimTable);
            }
        }

        if (data.getStatus() != null) {
            V1PersistentVolumeStatus status = data.getStatus();
            builder.status(status.getPhase());
        }
    }

}
