package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.JobDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.JobTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.BatchV1JobTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.BatchV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.JobService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Seconds;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobServiceImpl implements JobService {

    private final ApiClient k8sApiClient;
    private final BatchV1ApiExtendHandler batchV1Api;
    private final EventService eventService;
    private final PodService podService;
    private final Yaml yaml;
    private final K8sObjectManager k8sObjectManager;

    public JobServiceImpl(ApiClient k8sApiClient, EventService eventService, PodService podService, Yaml yaml,
                          K8sObjectManager k8sObjectManager) {
        this.k8sApiClient = k8sApiClient;
        this.batchV1Api = new BatchV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.podService = podService;
        this.yaml = yaml;
        this.k8sObjectManager = k8sObjectManager;
    }


    @SneakyThrows
    @Override
    public List<JobTable> allNamespaceJobTables() {
        ApiResponse<BatchV1JobTableList> apiResponse = batchV1Api.allNamespaceJobAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            BatchV1JobTableList statefulSets = apiResponse.getData();
            List<JobTable> dataTable = statefulSets.getDataTable();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<JobTable> jobs(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceJobTables();
        }
        ApiResponse<BatchV1JobTableList> apiResponse = batchV1Api.namespaceJobAsTable(namespace, "true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            BatchV1JobTableList statefulSets = apiResponse.getData();
            return statefulSets.getDataTable();
        }
        return Collections.emptyList();

    }

    @SneakyThrows
    @Override
    public Optional<JobDescribe> job(String namespace, String name) {

        ApiResponse<V1Job> apiResponse = batchV1Api.readNamespacedJobWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        JobDescribe.JobDescribeBuilder builder = JobDescribe.builder();
        V1Job data = apiResponse.getData();
        setJob(builder, data);
        JobDescribe jobDescribe = builder.build();

        List<PodTable> pods = podService.podTables(jobDescribe.getNamespace(), jobDescribe.getSelector());
        if (CollectionUtils.isNotEmpty(pods)) {
            jobDescribe.setPods(pods);
        }

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("Job",
            jobDescribe.getNamespace(), jobDescribe.getName(), jobDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> jobDescribe.setEvents(v1EventTableList.getDataTable()));


        return Optional.of(jobDescribe);
    }

    private void setJob(JobDescribe.JobDescribeBuilder builder, V1Job data) {

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
            V1JobSpec spec = data.getSpec();
            builder.parallelism(spec.getParallelism());
            builder.completions(spec.getCompletions());
            builder.backoffLimit(spec.getBackoffLimit());
            setSelector(builder, spec);
            setTemplate(builder, spec.getTemplate());
        }

        if (data.getStatus() != null) {
            V1JobStatus status = data.getStatus();
            builder.startTime(status.getStartTime());
            builder.creationTimestamp(status.getCompletionTime());
            if (status.getStartTime() != null && status.getCompletionTime() != null) {
                Seconds seconds = Seconds.secondsBetween(status.getStartTime(), status.getCompletionTime());
                builder.duration(seconds.toStandardDuration());
            }
            setJobStatus(builder, status);
        }

    }

    private void setTemplate(JobDescribe.JobDescribeBuilder builder, V1PodTemplateSpec template) {
        if (template.getSpec() != null && template.getSpec().getContainers() != null) {
            List<V1Container> containers = template.getSpec().getContainers();
            String dump = yaml.dump(containers);
            builder.podTemplate(Collections.singletonList(dump));
        }
    }

    private void setJobStatus(JobDescribe.JobDescribeBuilder builder, V1JobStatus status) {
        if (status.getSucceeded() != null) {
            builder.jobStatus("Succeeded : " + status.getSucceeded());
        } else if (status.getActive() != null) {
            builder.jobStatus("Active : " + status.getActive());
        } else if (status.getFailed() != null) {
            builder.jobStatus("Failed : " + status.getFailed());
        } else {
            builder.jobStatus(ExternalConstants.NONE);
        }
    }

    private void setSelector(JobDescribe.JobDescribeBuilder builder, V1JobSpec spec) {
        V1LabelSelector selector = spec.getSelector();
        if (selector == null) {
            return;
        }

        if (selector.getMatchLabels() != null) {
            builder.selector(selector.getMatchLabels());
        } else {
            if (CollectionUtils.isNotEmpty(selector.getMatchExpressions())) {
                Map<String, String> selectMatchExpr = selector.getMatchExpressions().stream()
                    .filter(requirement -> Objects.nonNull(requirement.getValues()) && Objects.nonNull(requirement.getKey()))
                    .collect(Collectors.toMap(V1LabelSelectorRequirement::getKey, requirement -> {
                        String values = String.join(", ", requirement.getValues());
                        return values + "(" + requirement.getOperator() + ")";
                    }));
                builder.selector(selectMatchExpr);
            }
        }
    }
}
