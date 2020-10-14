package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.DaemonSetDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.DaemonSetTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ObjectUsageResource;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AppsV1DaemonSetTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.AppsV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.DaemonSetService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodService;
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
public class DaemonSetServiceImpl implements DaemonSetService {

    private final ApiClient k8sApiClient;
    private final AppsV1ApiExtendHandler appsV1Api;
    private final EventService eventService;
    private final PodService podService;

    public DaemonSetServiceImpl(ApiClient k8sApiClient, EventService eventService, PodService podService) {
        this.k8sApiClient = k8sApiClient;
        this.appsV1Api = new AppsV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.podService = podService;
    }

    @SneakyThrows
    @Override
    public List<DaemonSetTable> allNamespaceDaemonSetTables() {
        ApiResponse<AppsV1DaemonSetTableList> apiResponse = appsV1Api.allNamespaceDaemonSetAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            AppsV1DaemonSetTableList deployments = apiResponse.getData();
            return deployments.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<DaemonSetDescribe> daemonSet(String namespace, String name) {
        ApiResponse<V1DaemonSet> apiResponse = appsV1Api.readNamespacedDaemonSetWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        DaemonSetDescribe.DaemonSetDescribeBuilder builder = DaemonSetDescribe.builder();
        V1DaemonSet data = apiResponse.getData();
        setDeployment(builder, data);
        DaemonSetDescribe daemonSetDescribe = builder.build();

        List<PodTable> pods = podService.podTables(daemonSetDescribe.getNamespace(), daemonSetDescribe.getSelector());
        if (CollectionUtils.isNotEmpty(pods)) {
            daemonSetDescribe.setPods(pods);
        }

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("DaemonSet",
            daemonSetDescribe.getNamespace(), daemonSetDescribe.getName(), daemonSetDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> daemonSetDescribe.setEvents(v1EventTableList.getDataTable()));

        return Optional.of(daemonSetDescribe);
    }

    private void setDeployment(DaemonSetDescribe.DaemonSetDescribeBuilder builder, V1DaemonSet data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            V1DaemonSetSpec spec = data.getSpec();

            if (spec.getTemplate().getSpec() != null) {
                builder.tolerations(spec.getTemplate().getSpec().getTolerations());
            }

            setStrategy(builder, spec);
            setSelector(builder, spec);
            setContainersImageAndResource(builder, spec);

        }

        if (data.getStatus() != null) {
            V1DaemonSetStatus status = data.getStatus();
            if (status.getNumberAvailable() != null) {
                builder.podStatus(status.getNumberAvailable().toString() + "/" + status.getDesiredNumberScheduled().toString());
            } else {
                builder.podStatus("/" + status.getDesiredNumberScheduled().toString());
            }
            builder.conditions(status.getConditions());
        }

    }

    private void setStrategy(DaemonSetDescribe.DaemonSetDescribeBuilder builder, V1DaemonSetSpec spec) {
        V1DaemonSetUpdateStrategy updateStrategy = spec.getUpdateStrategy();
        if (updateStrategy != null) {
            String type = StringUtils.defaultString(updateStrategy.getType(), ExternalConstants.NONE);
            if (updateStrategy.getRollingUpdate() != null
                && updateStrategy.getRollingUpdate().getMaxUnavailable() != null) {
                type = type + " [maxUnavailable: "
                    + updateStrategy.getRollingUpdate().getMaxUnavailable().toString() + "]";
            }
            builder.strategy(type);
        }
    }

    private void setSelector(DaemonSetDescribe.DaemonSetDescribeBuilder builder, V1DaemonSetSpec spec) {
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

    private void setContainersImageAndResource(DaemonSetDescribe.DaemonSetDescribeBuilder builder, V1DaemonSetSpec spec) {
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
