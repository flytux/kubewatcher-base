package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ContainerExtends;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1PodTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.MetricService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static io.kubernetes.client.openapi.models.V1ContainerState.*;

@Slf4j
@Service
public class PodServiceImpl implements PodService {

    public static final String FIELD_SELECTOR_NODE_NAME_KEY = "status.phase!=Failed,status.phase!=Succeeded,spec.nodeName=";

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreApi;
    private final EventService eventService;
    private final MetricService metricService;
    private final K8sObjectManager k8sObjectManager;

    public PodServiceImpl(ApiClient k8sApiClient, EventService eventService, MetricService metricService, K8sObjectManager k8sObjectManager) {
        this.k8sApiClient = k8sApiClient;
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.metricService = metricService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<PodTable> allNamespacePodTables() {
        ApiResponse<V1PodTableList> apiResponse = coreApi.allNamespacePodAsTable("true", null, null);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1PodTableList pod = apiResponse.getData();
            List<PodTable> dataTable = pod.getDataTable();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @Override
    public List<PodTable> podTables(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespacePodTables();
        }
        return podTables(namespace, Collections.emptyMap());
    }

    @SneakyThrows
    @Override
    public List<PodTable> podTables(String namespace, Map<String, String> selector) {

        // TODO matchExpressions 일 경우 확인이 필요함.
        String labelSelectors = selector.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining(","));

        ApiResponse<V1PodTableList> apiResponse = coreApi.namespacePodAsTable(namespace,null, labelSelectors, "true");

        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1PodTableList pod = apiResponse.getData();
            List<PodTable> podTables =  pod.getDataTable();
            List<MetricTable> metricTables = metricService.podMetrics(namespace, selector);

            for (PodTable podTable : podTables) {
                 for (MetricTable metricTable : metricTables) {
                    if (podTable.getName().equals(metricTable.getName())) {
                        podTable.setCpu(metricTable.getCpu());
                        podTable.setMemory(metricTable.getMemory());
                    }
                }
            }
            return podTables;
        }

        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<PodDescribe> pod(String namespace, String podName) {
        ApiResponse<V1Pod> podApiResponse = coreApi.readNamespacedPodWithHttpInfo(podName, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(podApiResponse.getStatusCode())) {
            return Optional.empty();
        }

        PodDescribe.PodDescribeBuilder builder = PodDescribe.builder();
        V1Pod pod = podApiResponse.getData();
        setPod(builder, pod);

        PodDescribe podDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("Pod",
            podDescribe.getNamespace(), podDescribe.getName(), podDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> podDescribe.setEvents(v1EventTableList.getDataTable()));

        return Optional.of(podDescribe);
    }

    @Override
    public Optional<V1PodList> nodePods(String nodeName) {

        String nodeFieldSelector = FIELD_SELECTOR_NODE_NAME_KEY + nodeName;
        ApiResponse<V1PodList> podsResponse = pods(nodeFieldSelector, null);
        if (ExternalConstants.isSuccessful(podsResponse.getStatusCode())) {
            V1PodList data = podsResponse.getData();
            return Optional.ofNullable(data);
        }
        return Optional.empty();
    }

    @Override
    public List<String> containers(String namespace, String podName) {
        Optional<PodDescribe> podOptional = pod(namespace, podName);
        return podOptional
            .map(podDescribe -> podDescribe.getContainers().stream()
                    .map(V1Container::getName)
                    .collect(Collectors.toList())
            ).orElse(Collections.emptyList());
    }


    @SneakyThrows
    private ApiResponse<V1PodList> pods(String fieldSelector, String labelSelector) {
        return coreApi.listPodForAllNamespacesWithHttpInfo(null, null,
            fieldSelector, labelSelector,
            500, "true",
            null, 0,
            Boolean.FALSE);
    }

    private void setPod(PodDescribe.PodDescribeBuilder builder, V1Pod pod) {
        if (pod.getMetadata() != null) {
            V1ObjectMeta metadata = pod.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.creationTimestamp(metadata.getCreationTimestamp());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
        }

        if (pod.getSpec() != null) {
            V1PodSpec spec = pod.getSpec();
            builder.node(spec.getNodeName());

            List<V1Container> containers = spec.getContainers();
            List<V1ContainerExtends> containerExtends = containers.stream()
                .map(v1Container -> {
                    V1ContainerExtends v1ContainerExtends = new V1ContainerExtends(v1Container);

                    if (pod.getStatus() != null) {
                        List<V1ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
                        if (CollectionUtils.isNotEmpty(containerStatuses)) {
                            Optional<V1ContainerState> stateOptional = containerStatuses.stream()
                                .filter(status -> StringUtils.equalsIgnoreCase(status.getName(), v1Container.getName()))
                                .findFirst().map(V1ContainerStatus::getState);
                            stateOptional.ifPresent(state -> {
                                if (state.getRunning() != null) {
                                    v1ContainerExtends.setStatus(SERIALIZED_NAME_RUNNING);
                                    return;
                                }
                                if (state.getTerminated() != null) {
                                    v1ContainerExtends.setStatus(SERIALIZED_NAME_TERMINATED);
                                    return;
                                }

                                if (state.getWaiting() != null) {
                                    v1ContainerExtends.setStatus(SERIALIZED_NAME_WAITING);
                                }
                            });
                        }
                    }
                    return v1ContainerExtends;
                })

                .collect(Collectors.toList());

            builder.containers(containerExtends);
            builder.volumes(spec.getVolumes());
            builder.tolerations(spec.getTolerations());
            builder.priority(spec.getPriorityClassName());
        }

        if (pod.getStatus() != null) {
            V1PodStatus status = pod.getStatus();
            builder.conditions(status.getConditions());
            builder.podIp(status.getPodIP());
            builder.status(status.getPhase());
        }
    }
}
