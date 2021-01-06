package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ObjectUsageResource;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.StatefulSetDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.StatefulSetTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AppsV1StatefulSetTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.AppsV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.StatefulSetService;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatefulSetServiceImpl implements StatefulSetService {

    private final ApiClient k8sApiClient;
    private final AppsV1ApiExtendHandler appsV1Api;
    private final EventService eventService;
    private final PodService podService;
    private final K8sObjectManager k8sObjectManager;

    public StatefulSetServiceImpl(ApiClient k8sApiClient, EventService eventService, PodService podService,
                                  K8sObjectManager k8sObjectManager) {
        this.k8sApiClient = k8sApiClient;
        this.appsV1Api = new AppsV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.podService = podService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<StatefulSetTable> allNamespaceStatefulSetTables() {
        ApiResponse<AppsV1StatefulSetTableList> apiResponse = appsV1Api.allNamespaceStatefulSetAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            AppsV1StatefulSetTableList statefulSets = apiResponse.getData();
            List<StatefulSetTable> dataTable = statefulSets.getDataTable();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }


    @SneakyThrows
    @Override
    public List<StatefulSetTable> statefulSets(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceStatefulSetTables();
        }
        ApiResponse<AppsV1StatefulSetTableList> apiResponse = appsV1Api.namespaceStatefulSetAsTable(namespace, "true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            AppsV1StatefulSetTableList statefulSets = apiResponse.getData();
            return statefulSets.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<StatefulSetDescribe> statefulSet(String namespace, String name) {
        ApiResponse<V1StatefulSet> apiResponse = appsV1Api.readNamespacedStatefulSetWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        StatefulSetDescribe.StatefulSetDescribeBuilder builder = StatefulSetDescribe.builder();
        V1StatefulSet data = apiResponse.getData();
        setStatefulSet(builder, data);
        StatefulSetDescribe statefulSetDescribe = builder.build();

        List<PodTable> pods = podService.podTables(statefulSetDescribe.getNamespace(), statefulSetDescribe.getTemplateLabels());
        if (CollectionUtils.isNotEmpty(pods)) {
            statefulSetDescribe.setPods(pods);
        }

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("StatefulSet",
            statefulSetDescribe.getNamespace(), statefulSetDescribe.getName(), statefulSetDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> statefulSetDescribe.setEvents(v1EventTableList.getDataTable()));


        return Optional.of(statefulSetDescribe);
    }

    private void setStatefulSet(StatefulSetDescribe.StatefulSetDescribeBuilder builder, V1StatefulSet data) {

        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            V1StatefulSetSpec spec = data.getSpec();

            if (spec.getTemplate().getSpec() != null) {
                builder.tolerations(spec.getTemplate().getSpec().getTolerations());
            }

            setStrategy(builder, spec);
            setSelector(builder, spec);
            setContainersImageAndResource(builder, spec);
            if (data.getSpec().getTemplate().getMetadata() != null) {
                builder.templateLabels(data.getSpec().getTemplate().getMetadata().getLabels());
            }
        }

        if (data.getStatus() != null) {
            if (data.getStatus() != null) {
                V1StatefulSetStatus status = data.getStatus();
                if (status.getCurrentReplicas() != null) {
                    builder.podStatus(status.getCurrentReplicas().toString() + "/" + status.getReplicas().toString());
                } else {
                    builder.podStatus("-/" + status.getReplicas().toString());
                }
                builder.conditions(status.getConditions());
            }
        }
    }

    private void setStrategy(StatefulSetDescribe.StatefulSetDescribeBuilder builder, V1StatefulSetSpec spec) {
        V1StatefulSetUpdateStrategy updateStrategy = spec.getUpdateStrategy();
        if (updateStrategy != null) {
            String type = StringUtils.defaultString(updateStrategy.getType(), ExternalConstants.NONE);
            if (updateStrategy.getRollingUpdate() != null
                && updateStrategy.getRollingUpdate().getPartition() != null) {
                type = type + " [partition: "
                    + updateStrategy.getRollingUpdate().getPartition().toString() + "]";
            }
            builder.strategy(type);
        }
    }

    private void setSelector(StatefulSetDescribe.StatefulSetDescribeBuilder builder, V1StatefulSetSpec spec) {
        V1LabelSelector selector = spec.getSelector();
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

    private void setContainersImageAndResource(StatefulSetDescribe.StatefulSetDescribeBuilder builder, V1StatefulSetSpec spec) {
        V1PodTemplateSpec template = spec.getTemplate();
        V1PodSpec templateSpec = template.getSpec();

        if (templateSpec == null) {
            return;
        }

        ObjectUsageResource resource = new ObjectUsageResource();

        StringJoiner imagesJoiner = new StringJoiner(",");

        templateSpec.getContainers().stream()
            .map(container -> {
                imagesJoiner.add(container.getImage());
                return container.getResources();
            })
            .filter(Objects::nonNull)
            .forEach(requirement -> {
                Map<String, Quantity> requests = requirement.getRequests();
                if (MapUtils.isNotEmpty(requests)) {
                    requests.forEach((key, quantity) -> sumResource(resource.computeRequests(), key, quantity));
                }

                Map<String, Quantity> limits = requirement.getLimits();
                if (MapUtils.isNotEmpty(limits)) {
                    limits.forEach((key, quantity) -> sumResource(resource.computeLimits(), key, quantity));
                }
            });
        builder.images(imagesJoiner.toString());
        builder.resources(resource);
    }

    private void sumResource(Map<String, Quantity> resource, String key, Quantity quantity) {
        resource.merge(key, quantity, (quantity1, quantity2) -> {
            BigDecimal add = quantity1.getNumber().add(quantity2.getNumber());
            return new Quantity(add, quantity1.getFormat());
        });
    }
}
