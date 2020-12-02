package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.DeploymentDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.DeploymentTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ObjectUsageResource;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.AppsV1DeploymentTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.AppsV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.DeploymentService;
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
public class DeploymentServiceImpl implements DeploymentService {

    private final ApiClient k8sApiClient;
    private final AppsV1ApiExtendHandler appsV1Api;
    private final EventService eventService;
    private final PodService podService;


    public DeploymentServiceImpl(ApiClient k8sApiClient, EventService eventService, PodService podService) {
        this.k8sApiClient = k8sApiClient;
        this.appsV1Api = new AppsV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.podService = podService;
    }

    @SneakyThrows
    @Override
    public List<DeploymentTable> allNamespaceDeploymentTables() {
        ApiResponse<AppsV1DeploymentTableList> apiResponse = appsV1Api.allNamespaceDeploymentAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            AppsV1DeploymentTableList deployments = apiResponse.getData();
            return deployments.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<DeploymentDescribe> deployment(String namespace, String deploymentName) {
        ApiResponse<V1Deployment> apiResponse = appsV1Api.readNamespacedDeploymentWithHttpInfo(deploymentName, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        DeploymentDescribe.DeploymentDescribeBuilder builder = DeploymentDescribe.builder();
        V1Deployment data = apiResponse.getData();
        setDeployment(builder, data);

        DeploymentDescribe deploymentDescribe = builder.build();

        List<PodTable> pods = podService.podTables(deploymentDescribe.getNamespace(), deploymentDescribe.getSelector());
        if (CollectionUtils.isNotEmpty(pods)) {
            deploymentDescribe.setPods(pods);
        }

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("Deployment",
            deploymentDescribe.getNamespace(), deploymentDescribe.getName(), deploymentDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> deploymentDescribe.setEvents(v1EventTableList.getDataTable()));

        return Optional.of(deploymentDescribe);
    }


    private void setDeployment(DeploymentDescribe.DeploymentDescribeBuilder builder, V1Deployment data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            V1DeploymentSpec spec = data.getSpec();
            builder.replicas(spec.getReplicas());
            setStrategy(builder, spec);
            setSelector(builder, spec);

            setResources(builder, spec);
        }

        if (data.getStatus() != null) {
            V1DeploymentStatus status = data.getStatus();
            builder.conditions(status.getConditions());
        }
    }


    private void setResources(DeploymentDescribe.DeploymentDescribeBuilder builder, V1DeploymentSpec spec) {
        V1PodTemplateSpec template = spec.getTemplate();
        V1PodSpec templateSpec = template.getSpec();

        if (templateSpec == null) {
            return;
        }

        ObjectUsageResource resource = new ObjectUsageResource();
        templateSpec.getContainers().stream()
            .map(V1Container::getResources)
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
        builder.resources(resource);
    }

    private void sumResource(Map<String, Quantity> resource, String key, Quantity quantity) {
        resource.merge(key, quantity, (quantity1, quantity2) -> {
            BigDecimal add = quantity1.getNumber().add(quantity2.getNumber());
            return new Quantity(add, quantity1.getFormat());
        });
    }

    private void setSelector(DeploymentDescribe.DeploymentDescribeBuilder builder, V1DeploymentSpec spec) {
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

    private void setStrategy(DeploymentDescribe.DeploymentDescribeBuilder builder, V1DeploymentSpec spec) {
        if (spec.getStrategy() != null) {
            String type = StringUtils.defaultString(spec.getStrategy().getType(), ExternalConstants.NONE);
            if (spec.getStrategy().getRollingUpdate() != null) {
                type = type
                    + " [maxSurge: " + spec.getStrategy().getRollingUpdate().getMaxSurge().toString()
                    + ", maxUnavailable: " + spec.getStrategy().getRollingUpdate().getMaxUnavailable().toString()
                    + "]";
            }
            builder.strategy(type);
        }
    }

}
