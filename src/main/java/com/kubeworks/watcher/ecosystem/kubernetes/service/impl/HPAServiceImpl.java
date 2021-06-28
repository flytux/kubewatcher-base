package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.HPADescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.HPAMetric;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.HPATable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AutoScalingV1HPATableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.AutoscalingV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.HPAService;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.apis.AutoscalingV2beta2Api;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HPAServiceImpl implements HPAService {

    private final AutoscalingV1ApiExtendHandler autoscalingV1Api;
    private final EventService eventService;
    private final K8sObjectManager k8sObjectManager;

    @Autowired
    public HPAServiceImpl(ApiClient k8sApiClient, EventService eventService, K8sObjectManager k8sObjectManager) {
        this.autoscalingV1Api = new AutoscalingV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<HPATable> allNamespaceHPATables() {
        ApiResponse<AutoScalingV1HPATableList> apiResponse = autoscalingV1Api.searchHorizontalPodAutoScalersTableList();
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            AutoScalingV1HPATableList hpaTableList = apiResponse.getData();
            List<HPATable> dataTable = hpaTableList.createDataTableList();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<HPATable> hpa(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceHPATables();
        }
        ApiResponse<AutoScalingV1HPATableList> apiResponse = autoscalingV1Api.searchHorizontalPodAutoScalersTableList(namespace);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            AutoScalingV1HPATableList hpaTableList = apiResponse.getData();
            return hpaTableList.createDataTableList();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<HPADescribe> hpa(String namespace, String name) {

        Optional<HPADescribe> hpaDescribe;
        try {
            hpaDescribe = hpaV2beta2(namespace, name);
        } catch (ApiException e) {
            log.warn("failed hpaV2beta2");
            hpaDescribe = hpaV1(namespace, name);
        }

        hpaDescribe.ifPresent(hpa -> {
            Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("HorizontalPodAutoscaler",
                hpa.getNamespace(), hpa.getName(), hpa.getUid());
            eventTableListOptional.ifPresent(v1EventTableList -> hpa.setEvents(v1EventTableList.createDataTableList()));
        });

        return hpaDescribe;
    }

    private Optional<HPADescribe> hpaV2beta2(String namespace, String name) throws ApiException {
        final AutoscalingV2beta2Api v2beta2Api = autoscalingV1Api.getV2beta2Api();
        ApiResponse<V2beta2HorizontalPodAutoscaler> apiResponse = v2beta2Api.readNamespacedHorizontalPodAutoscalerWithHttpInfo(name, namespace, "true", Boolean.TRUE, Boolean.FALSE);

        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        HPADescribe.HPADescribeBuilder builder = HPADescribe.builder();
        V2beta2HorizontalPodAutoscaler data = apiResponse.getData();
        setHPA(builder, data);

        return Optional.of(builder.build());
    }

    private Optional<HPADescribe> hpaV1(String namespace, String name) throws ApiException {
        ApiResponse<V1HorizontalPodAutoscaler> apiResponse = autoscalingV1Api.readNamespacedHorizontalPodAutoscalerWithHttpInfo(name, namespace, "true", Boolean.TRUE, Boolean.FALSE);

        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        HPADescribe.HPADescribeBuilder builder = HPADescribe.builder();
        V1HorizontalPodAutoscaler data = apiResponse.getData();
        setHPA(builder, data);

        return Optional.of(builder.build());
    }

    private void setHPA(HPADescribe.HPADescribeBuilder builder, KubernetesObject data) {

        V1ObjectMeta metadata = data.getMetadata();
        if (metadata != null) {
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        setSpec(builder, data);
        setStatus(builder, data);
        setMetrics(builder, (V2beta2HorizontalPodAutoscaler)data);

    }

    private void setSpec(HPADescribe.HPADescribeBuilder builder, KubernetesObject data) {
        if (data instanceof V2beta2HorizontalPodAutoscaler) {
            V2beta2HorizontalPodAutoscaler v2beta2Hpa = (V2beta2HorizontalPodAutoscaler) data;
            if (v2beta2Hpa.getSpec() == null) {
                return;
            }
            V2beta2HorizontalPodAutoscalerSpec spec = v2beta2Hpa.getSpec();
            builder.reference(String.join("/",
                spec.getScaleTargetRef().getKind(),
                spec.getScaleTargetRef().getName()));
            builder.minReplicas(spec.getMinReplicas());
            builder.maxReplicas(spec.getMaxReplicas());

        } else if (data instanceof V1HorizontalPodAutoscaler) {
            V1HorizontalPodAutoscaler v1Hpa = (V1HorizontalPodAutoscaler) data;
            if (v1Hpa.getSpec() == null) {
                return;
            }
            V1HorizontalPodAutoscalerSpec spec = v1Hpa.getSpec();
            builder.reference(String.join("/",
                spec.getScaleTargetRef().getKind(),
                spec.getScaleTargetRef().getName()));
            builder.minReplicas(spec.getMinReplicas());
            builder.maxReplicas(spec.getMaxReplicas());
        } else {
            log.warn("Unimplemented type -> {}", data.getClass().getName());
        }
    }

    private void setStatus(HPADescribe.HPADescribeBuilder builder, KubernetesObject data) {
        if (data instanceof V2beta2HorizontalPodAutoscaler) {
            V2beta2HorizontalPodAutoscaler v2beta2Hpa = (V2beta2HorizontalPodAutoscaler) data;

            if (v2beta2Hpa.getStatus() == null) {
                return;
            }

            V2beta2HorizontalPodAutoscalerStatus status = v2beta2Hpa.getStatus();
            builder.currentReplicas(status.getCurrentReplicas());
            builder.desiredReplicas(status.getDesiredReplicas());
            builder.conditions(status.getConditions());
            builder.lastScaleTime(status.getLastScaleTime());

        } else if (data instanceof V1HorizontalPodAutoscaler) {
            V1HorizontalPodAutoscaler v1Hpa = (V1HorizontalPodAutoscaler) data;

            if (v1Hpa.getStatus() == null) {
                return;
            }

            V1HorizontalPodAutoscalerStatus status = v1Hpa.getStatus();
            builder.currentReplicas(status.getCurrentReplicas());
            builder.desiredReplicas(status.getDesiredReplicas());
            builder.lastScaleTime(status.getLastScaleTime());
        } else {
            log.warn("Unimplemented type -> {}", data.getClass().getName());
        }
    }

    private void setMetrics(HPADescribe.HPADescribeBuilder builder, V2beta2HorizontalPodAutoscaler data) {

        V2beta2HorizontalPodAutoscalerSpec spec = data.getSpec();

        if (spec == null || CollectionUtils.isEmpty(spec.getMetrics())) {
            return;
        }

        List<V2beta2MetricStatus> currentMetrics = Collections.emptyList();
        if (data.getStatus() != null && CollectionUtils.isNotEmpty(data.getStatus().getCurrentMetrics())) {
            currentMetrics = data.getStatus().getCurrentMetrics();
        }

        Map<String, List<V2beta2MetricStatus>> currentMetricGroups = currentMetrics.stream().collect(Collectors.groupingBy(V2beta2MetricStatus::getType));

        Map<String, List<HPAMetric>> metricGroups = spec.getMetrics().stream()
            .map(metricSpec -> convertHPAMetric(metricSpec, currentMetricGroups.get(metricSpec.getType())))
            .collect(Collectors.groupingBy(HPAMetric::getType));

        builder.metrics(metricGroups);

    }

    private void processResourceType(
        final HPAMetric.HPAMetricBuilder builder, final V2beta2MetricSpec spec, final List<V2beta2MetricStatus> statuses) {

        if (Objects.nonNull(spec.getResource())) {
            builder.name(spec.getResource().getName()).target(spec.getResource().getTarget());

            if (Objects.nonNull(statuses)) {
                statuses.stream().map(V2beta2MetricStatus::getResource)
                    .filter(status -> Objects.nonNull(status) && StringUtils.equals(status.getName(), spec.getResource().getName()))
                    .findFirst().ifPresent(current -> builder.status(current.getCurrent()));
            }
        }
    }

    private void processObjectType(
        final HPAMetric.HPAMetricBuilder builder, final V2beta2MetricSpec spec, final List<V2beta2MetricStatus> statuses) {

        if (Objects.nonNull(spec.getObject())) {
            final V2beta2ObjectMetricSource object = spec.getObject();

            builder.name(object.getMetric().getName()).metric(object.getMetric()).target(object.getTarget());
            builder.type(spec.getType() + "(" + object.getDescribedObject().getKind() + "/" + object.getDescribedObject().getName() + ")");

            if (Objects.nonNull(statuses)) {
                statuses.stream().map(V2beta2MetricStatus::getObject).filter(createPredicate(object)).findFirst().ifPresent(s -> builder.status(s.getCurrent()));
            }
        }
    }

    private Predicate<V2beta2ObjectMetricStatus> createPredicate(final V2beta2ObjectMetricSource source) {

        return status -> Objects.nonNull(status)
            && StringUtils.equals(status.getMetric().getName(), source.getMetric().getName())
            && StringUtils.equals(status.getDescribedObject().getKind(), source.getDescribedObject().getKind())
            && StringUtils.equals(status.getDescribedObject().getName(), source.getDescribedObject().getName());
    }

    private void processPodsType(
        final HPAMetric.HPAMetricBuilder builder, final V2beta2MetricSpec spec, final List<V2beta2MetricStatus> statuses) {

        if (Objects.nonNull(spec.getPods())) {
            builder.name(spec.getPods().getMetric().getName()).metric(spec.getPods().getMetric()).target(spec.getPods().getTarget());

            if (Objects.nonNull(statuses)) {
                statuses.stream().map(V2beta2MetricStatus::getPods)
                    .filter(status -> Objects.nonNull(status) && StringUtils.equals(status.getMetric().getName(), spec.getPods().getMetric().getName()))
                    .findFirst().ifPresent(s -> builder.status(s.getCurrent()));
            }
        }
    }

    private void processExternalType(
        final HPAMetric.HPAMetricBuilder builder, final V2beta2MetricSpec spec, final List<V2beta2MetricStatus> statuses) {

        if (Objects.nonNull(spec.getExternal())) {
            final V2beta2ExternalMetricSource external = spec.getExternal();

            builder.name(external.getMetric().getName()).metric(external.getMetric()).target(external.getTarget());

            if (Objects.nonNull(statuses)) {
                statuses.stream().map(V2beta2MetricStatus::getExternal)
                    .filter(status -> Objects.nonNull(status) && StringUtils.equals(status.getMetric().getName(), external.getMetric().getName()))
                    .findFirst().ifPresent(s -> builder.status(s.getCurrent()));
            }
        }
    }

    private HPAMetric convertHPAMetric(final V2beta2MetricSpec spec, final List<V2beta2MetricStatus> statuses) {

        final HPAMetric.HPAMetricBuilder builder = HPAMetric.builder().type(spec.getType());

        switch (spec.getType()) {
            case "Resource":
                processResourceType(builder, spec, statuses); break;
            case "Object":
                processObjectType(builder, spec, statuses); break;
            case "Pods":
                processPodsType(builder, spec, statuses); break;
            case "External":
                processExternalType(builder, spec, statuses); break;
            default: break;
        }

        return builder.build();
    }
}
