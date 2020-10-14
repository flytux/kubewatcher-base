package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CronJobDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CronJobTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.BatchV1beta1CronJobTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.BatchV1beta1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.CronJobService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CronJobServiceImpl implements CronJobService {

    private final ApiClient k8sApiClient;
    private final BatchV1beta1ApiExtendHandler batchV1beta1Api;
    private final EventService eventService;
    private final PodService podService;
    private final Yaml yaml;

    public CronJobServiceImpl(ApiClient k8sApiClient, EventService eventService, PodService podService, Yaml yaml) {
        this.k8sApiClient = k8sApiClient;
        this.batchV1beta1Api = new BatchV1beta1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.podService = podService;
        this.yaml = yaml;
    }

    @SneakyThrows
    @Override
    public List<CronJobTable> allNamespaceCronJobTables() {
        ApiResponse<BatchV1beta1CronJobTableList> apiResponse = batchV1beta1Api.allNamespaceCronJobAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            BatchV1beta1CronJobTableList statefulSets = apiResponse.getData();
            return statefulSets.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<CronJobDescribe> cronJob(String namespace, String name) {
        ApiResponse<V1beta1CronJob> apiResponse = batchV1beta1Api.readNamespacedCronJobWithHttpInfo(name, namespace, "true", true, false);

        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        CronJobDescribe.CronJobDescribeBuilder builder = CronJobDescribe.builder();
        V1beta1CronJob data = apiResponse.getData();
        setCronJob(builder, data);

        CronJobDescribe cronJobDescribe = builder.build();


        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("CronJob",
            cronJobDescribe.getNamespace(), cronJobDescribe.getName(), cronJobDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> cronJobDescribe.setEvents(v1EventTableList.getDataTable()));

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
            String dump = yaml.dump(containers);
            builder.podTemplate(Collections.singletonList(dump));
        }
    }

}
