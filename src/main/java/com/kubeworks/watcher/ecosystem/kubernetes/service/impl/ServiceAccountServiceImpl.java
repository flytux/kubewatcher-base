package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.SecretDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceAccountDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceAccountTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ServiceAccountTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.SecretService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ServiceAccountService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1ObjectReference;
import io.kubernetes.client.openapi.models.V1ServiceAccount;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServiceAccountServiceImpl implements ServiceAccountService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreApi;
    private final EventService eventService;
    private final SecretService secretService;
    private final K8sObjectManager k8sObjectManager;

    public ServiceAccountServiceImpl(ApiClient k8sApiClient, EventService eventService, SecretService secretService,
                                     K8sObjectManager k8sObjectManager) {
        this.k8sApiClient = k8sApiClient;
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.secretService = secretService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<ServiceAccountTable> allNamespaceServiceAccountTables() {
        ApiResponse<V1ServiceAccountTableList> apiResponse = coreApi.allNamespaceServiceAccountAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ServiceAccountTableList serviceAccounts = apiResponse.getData();
            List<ServiceAccountTable> dataTable = serviceAccounts.getDataTable();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<ServiceAccountTable> serviceAccounts(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceServiceAccountTables();
        }
        ApiResponse<V1ServiceAccountTableList> apiResponse = coreApi.namespaceServiceAccountAsTables(namespace, "true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ServiceAccountTableList serviceAccounts = apiResponse.getData();
            return serviceAccounts.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<ServiceAccountDescribe> serviceAccount(String namespace, String name) {
        ApiResponse<V1ServiceAccount> apiResponse = coreApi.readNamespacedServiceAccountWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        ServiceAccountDescribe.ServiceAccountDescribeBuilder builder = ServiceAccountDescribe.builder();
        V1ServiceAccount data = apiResponse.getData();
        setServiceAccount(builder, data);

        ServiceAccountDescribe serviceAccountDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("ServiceAccount",
            serviceAccountDescribe.getNamespace(), serviceAccountDescribe.getName(), serviceAccountDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> serviceAccountDescribe.setEvents(v1EventTableList.getDataTable()));


        List<String> secretNames = getSecretNames(data);

        List<SecretDescribe> secrets = secretService.secretTable(serviceAccountDescribe.getNamespace());

        List<SecretDescribe> filteredSecrets =secrets.stream()
            .filter(secretDescribe -> secretNames.contains(secretDescribe.getName()))
            .collect(Collectors.toList());

        serviceAccountDescribe.setSecrets(filteredSecrets);

        return Optional.of(serviceAccountDescribe);
    }


    private void setServiceAccount(ServiceAccountDescribe.ServiceAccountDescribeBuilder builder, V1ServiceAccount data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }
    }

    private List<String> getSecretNames(V1ServiceAccount data) {
        if (data.getSecrets() != null) {
            List<V1ObjectReference> secrets = data.getSecrets();
            return secrets.stream().map(V1ObjectReference::getName).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


}
