package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1NetworkPolicyTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.NetworkingV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.NetworkPolicyService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NetworkPolicyServiceImpl implements NetworkPolicyService {

    private final ApiClient k8sApiClient;
    private final NetworkingV1ApiExtendHandler networkApi;
    private final EventService eventService;

    public NetworkPolicyServiceImpl(ApiClient k8sApiClient, EventService eventService) {
        this.k8sApiClient = k8sApiClient;
        this.networkApi = new NetworkingV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
    }

    @SneakyThrows
    @Override
    public List<NetworkPolicyTable> allNamespaceNetworkPolicyTables() {
        ApiResponse<NetworkingV1NetworkPolicyTableList> apiResponse = networkApi.allNamespaceNetworkPolicyAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            NetworkingV1NetworkPolicyTableList networkpolicies = apiResponse.getData();
            return networkpolicies.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<NetworkPolicyDescribe> networkPolicy(String namespace, String name) {
        ApiResponse<V1NetworkPolicy> apiResponse = networkApi.readNamespacedNetworkPolicyWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder = NetworkPolicyDescribe.builder();
        V1NetworkPolicy data = apiResponse.getData();
        setService(builder, data);

        NetworkPolicyDescribe networkPolicyDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("NetworkPolicy",
            networkPolicyDescribe.getNamespace(), networkPolicyDescribe.getName(), networkPolicyDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> networkPolicyDescribe.setEvents(v1EventTableList.getDataTable()));

        return Optional.of(networkPolicyDescribe);
    }

    private void setService(NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder, V1NetworkPolicy data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.labels(metadata.getLabels());
            builder.uid(metadata.getUid());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            V1NetworkPolicySpec spec = data.getSpec();
            builder.specs(spec);
        }

    }
}
