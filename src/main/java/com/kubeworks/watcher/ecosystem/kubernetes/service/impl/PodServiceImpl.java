package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ContainerExtends;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.kubernetes.client.openapi.models.V1ContainerState.*;

@Slf4j
@Service
public class PodServiceImpl implements PodService {

    public static final String FIELD_SELECTOR_NODE_NAME_KEY = "status.phase!=Failed,status.phase!=Succeeded,spec.nodeName=";

    private final K8sObjectManager manager;
    private final CoreV1ApiExtendHandler handler;
    private final EventService eventService;
    private final MetricService metricService;

    @Autowired
    public PodServiceImpl(final ApiClient client, final EventService eventService, final MetricService metricService, final K8sObjectManager manager) {

        this.manager = manager;
        this.handler = new CoreV1ApiExtendHandler(client);
        this.eventService = eventService;
        this.metricService = metricService;
    }

    @Override
    @SneakyThrows
    public List<PodTable> allNamespacePodTables() {

        final ApiResponse<V1PodTableList> response = handler.searchPodsTableList(null, null);

        if (ExternalConstants.isSuccessful(response.getStatusCode())) {
            final List<PodTable> dataTable = response.getData().createDataTableList();
            dataTable.sort((f, s) -> manager.compareByNamespace(f.getNamespace(), s.getNamespace()));

            return dataTable;
        }

        return Collections.emptyList();
    }

    @Override
    public List<PodTable> podTables(final String namespace) {

        if (!StringUtils.hasText(namespace) || "all".equalsIgnoreCase(namespace)) {
            return allNamespacePodTables();
        }

        return podTables(namespace, Collections.emptyMap());
    }

    @Override
    @SneakyThrows
    public List<PodTable> podTables(final String namespace, final Map<String, String> selector) {

        // TODO matchExpressions 일 경우 확인이 필요함.
        final ApiResponse<V1PodTableList> response = handler.searchPodsTableList(namespace, null,
            selector.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(",")));

        if (ExternalConstants.isSuccessful(response.getStatusCode())) {
            final List<PodTable> podTables =  response.getData().createDataTableList();
            final List<MetricTable> metricTables = metricService.podMetrics(namespace, selector);

            for (final PodTable podTable : podTables) {
                for (final MetricTable metricTable : metricTables) {
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

    @Override
    @SneakyThrows
    public Optional<PodDescribe> pod(final String namespace, final String podName) {

        final ApiResponse<V1Pod> response = handler.readNamespacedPodWithHttpInfo(podName, namespace, "true", Boolean.TRUE, Boolean.FALSE);

        if (!ExternalConstants.isSuccessful(response.getStatusCode())) {
            return Optional.empty();
        }

        final PodDescribe.PodDescribeBuilder builder = PodDescribe.builder();

        setPod(builder, response.getData());

        final PodDescribe pd = builder.build();
        eventService.eventTable("Pod", pd.getNamespace(), pd.getName(), pd.getUid()).ifPresent(o -> pd.setEvents(o.createDataTableList()));

        return Optional.of(pd);
    }

    @Override
    public Optional<V1PodList> nodePods(final String nodeName) {

        final ApiResponse<V1PodList> response = pods(FIELD_SELECTOR_NODE_NAME_KEY + nodeName);

        return ExternalConstants.isSuccessful(response.getStatusCode()) ? Optional.ofNullable(response.getData()) : Optional.empty();
    }

    @Override
    public List<String> containers(final String namespace, final String podName) {
        return pod(namespace, podName).map(pd -> pd.getContainers().stream().map(V1Container::getName).collect(Collectors.toList())).orElseGet(Collections::emptyList);
    }

    @SneakyThrows
    private ApiResponse<V1PodList> pods(final String fieldSelector) {
        return handler.listPodForAllNamespacesWithHttpInfo(null, null, fieldSelector, null, 500, "true", null, 0, Boolean.FALSE);
    }

    private void setPod(final PodDescribe.PodDescribeBuilder builder, final V1Pod pod) {

        if (Objects.nonNull(pod.getMetadata())) {
            final V1ObjectMeta meta = pod.getMetadata();

            builder.name(meta.getName());
            builder.namespace(meta.getNamespace());
            builder.uid(meta.getUid());
            builder.creationTimestamp(meta.getCreationTimestamp());
            builder.labels(meta.getLabels());
            builder.annotations(meta.getAnnotations());
        }

        if (Objects.nonNull(pod.getSpec())) {
            final V1PodSpec spec = pod.getSpec();

            builder.node(spec.getNodeName());
            builder.containers(convertContainers(spec.getContainers(), pod.getStatus()));
            builder.volumes(spec.getVolumes());
            builder.tolerations(spec.getTolerations());
            builder.priority(spec.getPriorityClassName());
        }

        if (Objects.nonNull(pod.getStatus())) {
            final V1PodStatus status = pod.getStatus();

            builder.podIp(status.getPodIP());
            builder.status(status.getPhase());
            builder.conditions(status.getConditions());
        }
    }

    private List<V1ContainerExtends> convertContainers(final List<V1Container> containers, final V1PodStatus status) {

        return containers.stream().map(c -> {
            final V1ContainerExtends extension = new V1ContainerExtends(c);

            if (Objects.nonNull(status)) {
                final List<V1ContainerStatus> statuses = status.getContainerStatuses();
                if (!CollectionUtils.isEmpty(statuses)) {
                    statuses.stream().filter(s -> c.getName().equalsIgnoreCase(s.getName()))
                        .findFirst().map(V1ContainerStatus::getState).ifPresent(retrieveStateConsumer(extension));
                }
            }

            return extension;
        }).collect(Collectors.toList());
    }

    private Consumer<V1ContainerState> retrieveStateConsumer(final V1ContainerExtends extension) {

        return s -> {
            if (Objects.nonNull(s.getRunning())) {
                extension.setStatus(SERIALIZED_NAME_RUNNING); return;
            }
            if (Objects.nonNull(s.getWaiting())) {
                extension.setStatus(SERIALIZED_NAME_WAITING); return;
            }
            if (Objects.nonNull(s.getTerminated())) {
                extension.setStatus(SERIALIZED_NAME_TERMINATED);
            }
        };
    }
}
