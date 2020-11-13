package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1beta1IngressTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.NetworkingV1beta1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EndpointService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.IngressService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class IngressServiceImpl implements IngressService {

    private final ApiClient k8sApiClient;
    private final NetworkingV1beta1ApiExtendHandler networkingApi;
    private final EventService eventService;
    private final EndpointService endpointService;

    public IngressServiceImpl(ApiClient k8sApiClient, EventService eventService, EndpointService endpointService) {
        this.k8sApiClient = k8sApiClient;
        this.networkingApi = new NetworkingV1beta1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.endpointService = endpointService;
    }

    @SneakyThrows
    @Override
    public List<IngressTable> allNamespaceIngressTables() {
        ApiResponse<NetworkingV1beta1IngressTableList> apiResponse = networkingApi.allNamespaceIngressAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            NetworkingV1beta1IngressTableList ingresses = apiResponse.getData();
            return ingresses.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<IngressDescribe> ingress(String namespace, String name) {
        ApiResponse<NetworkingV1beta1Ingress> apiResponse = networkingApi.readNamespacedIngressWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        IngressDescribe.IngressDescribeBuilder builder = IngressDescribe.builder();
        NetworkingV1beta1Ingress data = apiResponse.getData();
        setIngress(builder, data);

        IngressDescribe ingressDescribe = builder.build();

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("Ingress",
            ingressDescribe.getNamespace(), ingressDescribe.getName(), ingressDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> ingressDescribe.setEvents(v1EventTableList.getDataTable()));

        List<NetworkingV1beta1HTTPIngressPath> path = ingressDescribe.getRules().get(0).getHttp().getPaths();

        List<EndpointTable> endpoint = new ArrayList<>();

        for (int i=0; i < path.size(); i++) {
            String paths = path.get(i).getBackend().getServiceName();
            endpoint.addAll(endpointService.endpointTable(ingressDescribe.getNamespace(), paths));
            ingressDescribe.setEndpoints(endpoint);
        }

        return Optional.of(ingressDescribe);
    }

    private void setIngress(IngressDescribe.IngressDescribeBuilder builder, NetworkingV1beta1Ingress data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            NetworkingV1beta1IngressSpec spec = data.getSpec();
            builder.rules(spec.getRules());
        }

        if (data.getStatus() != null) {
            NetworkingV1beta1IngressStatus status = data.getStatus();
            builder.loadBalancer(status.getLoadBalancer());
        }
    }


}
