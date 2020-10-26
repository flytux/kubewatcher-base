package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.SecretDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.SecretTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1SecretTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.SecretService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Secret;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SecretServiceImpl implements SecretService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreV1Api;

    public SecretServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.coreV1Api = new CoreV1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<SecretTable> allNamespaceSecretTables() {
        ApiResponse<V1SecretTableList> apiResponse = coreV1Api.allNamespaceSecretAsTable("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1SecretTableList secrets = apiResponse.getData();
            return secrets.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<SecretDescribe> secret(String namespace, String name) {
        ApiResponse<V1Secret> apiResponse = coreV1Api.readNamespacedSecretWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        SecretDescribe.SecretDescribeBuilder builder = SecretDescribe.builder();
        V1Secret data = apiResponse.getData();
        setSecret(builder, data);

        SecretDescribe secretDescribe = builder.build();

        return Optional.of(secretDescribe);
    }

    private void setSecret(SecretDescribe.SecretDescribeBuilder builder, V1Secret data) {
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
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new String(entry.getValue())));
            builder.data(stringConvertData);
        }

        if (data.getStringData() != null) {
            builder.stringData(data.getStringData());
        }
    }

}
