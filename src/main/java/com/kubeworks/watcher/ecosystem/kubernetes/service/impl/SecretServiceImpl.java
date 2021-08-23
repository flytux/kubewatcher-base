package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.SecretDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.SecretTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1SecretTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.SecretService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.openapi.models.V1SecretList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SecretServiceImpl implements SecretService {

    private final CoreV1ApiExtendHandler coreV1Api;
    private final K8sObjectManager k8sObjectManager;

    @Autowired
    public SecretServiceImpl(ApiClient k8sApiClient, K8sObjectManager k8sObjectManager) {
        this.coreV1Api = new CoreV1ApiExtendHandler(k8sApiClient);
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<SecretTable> allNamespaceSecretTables() {
        ApiResponse<V1SecretTableList> apiResponse = coreV1Api.searchSecretsTableList();
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1SecretTableList secrets = apiResponse.getData();
            List<SecretTable> dataTable = secrets.createDataTableList();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<SecretTable> secrets(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceSecretTables();
        }
        ApiResponse<V1SecretTableList> apiResponse = coreV1Api.searchSecretsTableList(namespace);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1SecretTableList secrets = apiResponse.getData();
            return secrets.createDataTableList();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<SecretDescribe> secret(String namespace, String name) {
        ApiResponse<V1Secret> apiResponse = coreV1Api.readNamespacedSecretWithHttpInfo(name, namespace, "true", Boolean.TRUE, Boolean.FALSE);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        SecretDescribe.SecretDescribeBuilder builder = SecretDescribe.builder();
        V1Secret data = apiResponse.getData();
        setSecret(builder, data);

        SecretDescribe secretDescribe = builder.build();

        return Optional.of(secretDescribe);
    }

    @SneakyThrows
    @Override
    public List<SecretDescribe> secretTable(String namespace) {
        ApiResponse<V1SecretList> apiResponse = coreV1Api.listNamespacedSecretWithHttpInfo(namespace, "true", Boolean.TRUE, null,null,
            null,null,null,null,null);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Collections.emptyList();
        }

        V1SecretList data = apiResponse.getData();
        List<V1Secret> secrets = data.getItems();

        return secrets.stream().map(v1Secret -> {
            SecretDescribe.SecretDescribeBuilder builder1 = SecretDescribe.builder();
            setSecret(builder1, v1Secret);
            return builder1.build();
        }).collect(Collectors.toList());
    }



    private void setSecret(SecretDescribe.SecretDescribeBuilder builder, V1Secret data) {

        builder.type(data.getType());

        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getData() != null) {
            Map<String, byte[]> secretData = data.getData();
            Map<String, String> stringConvertData = secretData.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new String(entry.getValue(), StandardCharsets.UTF_8)));
            builder.data(stringConvertData);
        }

        if (data.getStringData() != null) {
            builder.stringData(data.getStringData());
        }
    }
}
