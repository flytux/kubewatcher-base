package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CustomResourceDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CustomResourceTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.ApiExtV1CustomResourceTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.ApiExtensionsV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.CustomResourceService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomResourceServiceImpl implements CustomResourceService {

    private final ApiClient k8sApiClient;
    private final ApiExtensionsV1ApiExtendHandler apiExtensionsV1Api;
    private final EventService eventService;

    public CustomResourceServiceImpl(ApiClient k8sApiClient, EventService eventService) {
        this.k8sApiClient = k8sApiClient;
        this.apiExtensionsV1Api = new ApiExtensionsV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
    }

    @SneakyThrows
    @Override
    public List<CustomResourceTable> allCustomResourceTables() {
        ApiResponse<ApiExtV1CustomResourceTableList> apiResponse = apiExtensionsV1Api.allCustomResourceAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            ApiExtV1CustomResourceTableList customResources = apiResponse.getData();
            return customResources.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<CustomResourceDescribe> customResource(String name) {
        ApiResponse<V1CustomResourceDefinition> apiResponse = apiExtensionsV1Api.readCustomResourceDefinitionWithHttpInfo(name, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        CustomResourceDescribe.CustomResourceDescribeBuilder builder = CustomResourceDescribe.builder();
        V1CustomResourceDefinition data = apiResponse.getData();
        setCustomResource(builder, data);

        CustomResourceDescribe customResourceDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("CustomResourceDefinition", "",
            customResourceDescribe.getName(), customResourceDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> customResourceDescribe.setEvents(v1EventTableList.getDataTable()));


        return Optional.of(customResourceDescribe);
    }

    private void setCustomResource(CustomResourceDescribe.CustomResourceDescribeBuilder builder, V1CustomResourceDefinition data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            V1CustomResourceDefinitionSpec spec = data.getSpec();
            builder.group(spec.getGroup());
            builder.scope(spec.getScope());

            setConversion(builder, spec);
            setVersions(builder, spec);

        }

        if (data.getStatus() != null) {
            V1CustomResourceDefinitionStatus status = data.getStatus();
            builder.conditions(status.getConditions());
            builder.acceptedNames(status.getAcceptedNames());
        }
    }

    private void setVersions(CustomResourceDescribe.CustomResourceDescribeBuilder builder, V1CustomResourceDefinitionSpec spec) {
        List<String> versionNames = spec.getVersions().stream()
            .map(V1CustomResourceDefinitionVersion::getName)
            .collect(Collectors.toList());
        builder.versions(versionNames);
    }

    private void setConversion(CustomResourceDescribe.CustomResourceDescribeBuilder builder, V1CustomResourceDefinitionSpec spec) {
        if (spec.getConversion() == null) {
            return;
        }
        V1CustomResourceConversion conversion = spec.getConversion();
        builder.conversion(conversion.getStrategy());
        if (StringUtils.equalsIgnoreCase("Webhook", conversion.getStrategy())
            && conversion.getWebhook() != null) {
            V1WebhookConversion webhook = conversion.getWebhook();
            String webhookYaml = ExternalConstants.yamlDump(webhook);
            builder.webhookYaml(webhookYaml);
        }
    }
}
