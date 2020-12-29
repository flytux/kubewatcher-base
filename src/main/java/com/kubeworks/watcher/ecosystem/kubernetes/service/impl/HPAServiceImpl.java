package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
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
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HPAServiceImpl implements HPAService {

    private final ApiClient k8sApiClient;
    private final AutoscalingV1ApiExtendHandler autoscalingV1Api;
    private final EventService eventService;

    public HPAServiceImpl(ApiClient k8sApiClient, EventService eventService) {
        this.k8sApiClient = k8sApiClient;
        this.autoscalingV1Api = new AutoscalingV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
    }

    @SneakyThrows
    @Override
    public List<HPATable> allNamespaceHPATables() {
        ApiResponse<AutoScalingV1HPATableList> apiResponse = autoscalingV1Api.allNamespaceHPAAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            AutoScalingV1HPATableList hpaTableList = apiResponse.getData();
            return hpaTableList.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<HPATable> hpa(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceHPATables();
        }
        ApiResponse<AutoScalingV1HPATableList> apiResponse = autoscalingV1Api.namespaceHPAAsTable(namespace, "true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            AutoScalingV1HPATableList hpaTableList = apiResponse.getData();
            return hpaTableList.getDataTable();
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
            eventTableListOptional.ifPresent(v1EventTableList -> hpa.setEvents(v1EventTableList.getDataTable()));
        });

        return hpaDescribe;
    }

    private Optional<HPADescribe> hpaV2beta2(String namespace, String name) throws ApiException {
        final AutoscalingV2beta2Api v2beta2Api = autoscalingV1Api.getV2beta2Api();
        ApiResponse<V2beta2HorizontalPodAutoscaler> apiResponse = v2beta2Api.readNamespacedHorizontalPodAutoscalerWithHttpInfo(name, namespace, "true", true, false);

        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        HPADescribe.HPADescribeBuilder builder = HPADescribe.builder();
        V2beta2HorizontalPodAutoscaler data = apiResponse.getData();
        setHPA(builder, data);

        return Optional.of(builder.build());
    }

    private Optional<HPADescribe> hpaV1(String namespace, String name) throws ApiException {
        ApiResponse<V1HorizontalPodAutoscaler> apiResponse = autoscalingV1Api.readNamespacedHorizontalPodAutoscalerWithHttpInfo(name, namespace, "true", true, false);

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
        setMetrics(builder, data);

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

        } else if(data instanceof V1HorizontalPodAutoscaler) {
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

        } else if(data instanceof V1HorizontalPodAutoscaler) {
            V1HorizontalPodAutoscaler v1Hpa = (V1HorizontalPodAutoscaler) data;

            if (v1Hpa.getStatus() == null) {
                return;
            }

            V1HorizontalPodAutoscalerStatus status = v1Hpa.getStatus();
            builder.currentReplicas(status.getCurrentReplicas());
            builder.desiredReplicas(status.getDesiredReplicas());
            builder.lastScaleTime(status.getLastScaleTime());
        }
    }

    private void setMetrics(HPADescribe.HPADescribeBuilder builder, KubernetesObject data) {

        V2beta2HorizontalPodAutoscaler v2beta2Hpa = (V2beta2HorizontalPodAutoscaler) data;

        V2beta2HorizontalPodAutoscalerSpec spec = v2beta2Hpa.getSpec();

        if (spec == null || CollectionUtils.isEmpty(spec.getMetrics())) {
            return;
        }

        List<V2beta2MetricStatus> currentMetrics = Collections.emptyList();
        if (v2beta2Hpa.getStatus() != null && CollectionUtils.isNotEmpty(v2beta2Hpa.getStatus().getCurrentMetrics())) {
            currentMetrics = v2beta2Hpa.getStatus().getCurrentMetrics();
        }

        Map<String, List<V2beta2MetricStatus>> currentMetricGroups = currentMetrics.stream().collect(Collectors.groupingBy(V2beta2MetricStatus::getType));

        Map<String, List<HPAMetric>> metricGroups = spec.getMetrics().stream()
            .map(metricSpec -> convertHPAMetric(metricSpec, currentMetricGroups.get(metricSpec.getType())))
            .collect(Collectors.groupingBy(HPAMetric::getType));

        builder.metrics(metricGroups);

    }

    private HPAMetric convertHPAMetric(V2beta2MetricSpec v2beta2MetricSpec, List<V2beta2MetricStatus> v2beta2MetricStatuses) {
        HPAMetric.HPAMetricBuilder metricBuilder = HPAMetric.builder();
        String type = v2beta2MetricSpec.getType();
        metricBuilder.type(type);
        switch (type) {
            case "Resource":
                if (v2beta2MetricSpec.getResource() != null) {
                    V2beta2ResourceMetricSource resource = v2beta2MetricSpec.getResource();
                    metricBuilder.name(resource.getName());
                    metricBuilder.target(resource.getTarget());

                    if (v2beta2MetricStatuses != null) {
                        Optional<V2beta2ResourceMetricStatus> currentOptional = v2beta2MetricStatuses.stream()
                            .map(V2beta2MetricStatus::getResource)
                            .filter(status -> Objects.nonNull(status) && StringUtils.equals(status.getName(), resource.getName()))
                            .findFirst();
                        currentOptional.ifPresent(current -> metricBuilder.status(current.getCurrent()));
                    }
                }
                break;
            case "Object":
                if (v2beta2MetricSpec.getObject() != null) {
                    V2beta2ObjectMetricSource object = v2beta2MetricSpec.getObject();
                    V2beta2CrossVersionObjectReference describedObject = object.getDescribedObject();
                    V2beta2MetricIdentifier metric = object.getMetric();

                    metricBuilder.type(type + "(" + String.join("/", describedObject.getKind(), describedObject.getName()) + ")");
                    metricBuilder.name(metric.getName());
                    metricBuilder.metric(metric);
                    metricBuilder.target(object.getTarget());

                    if (v2beta2MetricStatuses != null) {
                        Optional<V2beta2ObjectMetricStatus> currentOptional = v2beta2MetricStatuses.stream()
                            .map(V2beta2MetricStatus::getObject)
                            .filter(status -> Objects.nonNull(status)
                                && StringUtils.equals(status.getMetric().getName(), metric.getName())
                                && StringUtils.equals(status.getDescribedObject().getKind(), describedObject.getKind())
                                && StringUtils.equals(status.getDescribedObject().getName(), describedObject.getName())
                            ).findFirst();
                        currentOptional.ifPresent(current -> metricBuilder.status(current.getCurrent()));
                    }
                }
                break;
            case "Pods":
                if (v2beta2MetricSpec.getPods() != null) {
                    V2beta2PodsMetricSource pods = v2beta2MetricSpec.getPods();
                    V2beta2MetricIdentifier metric = pods.getMetric();
                    metricBuilder.name(metric.getName());
                    metricBuilder.metric(metric);
                    metricBuilder.target(pods.getTarget());

                    if (v2beta2MetricStatuses != null) {
                        Optional<V2beta2PodsMetricStatus> currentOptional = v2beta2MetricStatuses.stream()
                            .map(V2beta2MetricStatus::getPods)
                            .filter(status -> Objects.nonNull(status) && StringUtils.equals(status.getMetric().getName(), metric.getName()))
                            .findFirst();
                        currentOptional.ifPresent(current -> metricBuilder.status(current.getCurrent()));
                    }
                }
                break;
            case "External":
                if (v2beta2MetricSpec.getExternal() != null) {
                    V2beta2ExternalMetricSource external = v2beta2MetricSpec.getExternal();
                    V2beta2MetricIdentifier metric = external.getMetric();
                    metricBuilder.name(metric.getName());
                    metricBuilder.metric(metric);
                    metricBuilder.target(external.getTarget());

                    if (v2beta2MetricStatuses != null) {
                        Optional<V2beta2ExternalMetricStatus> currentOptional = v2beta2MetricStatuses.stream()
                            .map(V2beta2MetricStatus::getExternal)
                            .filter(status -> Objects.nonNull(status) && StringUtils.equals(status.getMetric().getName(), metric.getName()))
                            .findFirst();
                        currentOptional.ifPresent(current -> metricBuilder.status(current.getCurrent()));
                    }
                }
                break;
            default:
                break;
        }

        return metricBuilder.build();
    }

}
