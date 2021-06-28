package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.YamlHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CronJobDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CronJobTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.BatchV1beta1CronJobTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.BatchV1beta1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.CronJobService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CronJobServiceImpl implements CronJobService {

    private final BatchV1beta1ApiExtendHandler batchV1beta1Api;
    private final EventService eventService;
    private final K8sObjectManager k8sObjectManager;

    @Autowired
    public CronJobServiceImpl(ApiClient k8sApiClient, EventService eventService, K8sObjectManager k8sObjectManager) {
        this.batchV1beta1Api = new BatchV1beta1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<CronJobTable> allNamespaceCronJobTables() {
        ApiResponse<BatchV1beta1CronJobTableList> apiResponse = batchV1beta1Api.searchCronJobsTableList();
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            BatchV1beta1CronJobTableList statefulSets = apiResponse.getData();
            List<CronJobTable> dataTable = statefulSets.createDataTableList();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<CronJobTable> cronJobs(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceCronJobTables();
        }
        ApiResponse<BatchV1beta1CronJobTableList> apiResponse = batchV1beta1Api.searchCronJobsTableList();
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            BatchV1beta1CronJobTableList statefulSets = apiResponse.getData();
            return statefulSets.createDataTableList();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<CronJobDescribe> cronJob(String namespace, String name) {
        ApiResponse<V1beta1CronJob> apiResponse = batchV1beta1Api.readNamespacedCronJobWithHttpInfo(name, namespace, "true", Boolean.TRUE, Boolean.FALSE);

        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        CronJobDescribe.CronJobDescribeBuilder builder = CronJobDescribe.builder();
        V1beta1CronJob data = apiResponse.getData();
        setCronJob(builder, data);

        CronJobDescribe cronJobDescribe = builder.build();


        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("CronJob",
            cronJobDescribe.getNamespace(), cronJobDescribe.getName(), cronJobDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> cronJobDescribe.setEvents(v1EventTableList.createDataTableList()));

        return Optional.of(cronJobDescribe);
    }

    private void setCronJob(CronJobDescribe.CronJobDescribeBuilder builder, V1beta1CronJob data) {

        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            V1beta1CronJobSpec spec = data.getSpec();
            builder.schedule(spec.getSchedule());
            builder.suspend(spec.getSuspend());
            setTemplate(builder, spec.getJobTemplate());
        }

        if (data.getStatus() != null) {
            V1beta1CronJobStatus status = data.getStatus();
            if (CollectionUtils.isNotEmpty(status.getActive())) {
                builder.active(status.getActive().size());
            }
            builder.lastSchedule(status.getLastScheduleTime());
        }
    }

    private void setTemplate(CronJobDescribe.CronJobDescribeBuilder builder, V1beta1JobTemplateSpec jobTemplateSpec) {
        if (jobTemplateSpec.getSpec() != null && jobTemplateSpec.getSpec().getTemplate().getSpec() != null) {
            List<V1Container> containers = jobTemplateSpec.getSpec().getTemplate().getSpec().getContainers();
            builder.podTemplate(Collections.singletonList(YamlHandler.serialize(containers)));
        }
    }
}
