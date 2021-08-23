package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.IngressDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.IngressTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1beta1IngressTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.NetworkingV1beta1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EndpointService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.IngressService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ServiceKindService;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IngressServiceImpl implements IngressService {

    private final NetworkingV1beta1ApiExtendHandler networkingApi;
    private final EventService eventService;
    private final EndpointService endpointService;
    private final ServiceKindService serviceKindService;
    private final K8sObjectManager k8sObjectManager;

    @Autowired
    public IngressServiceImpl(ApiClient k8sApiClient, EventService eventService,
                              EndpointService endpointService, ServiceKindService serviceKindService, K8sObjectManager k8sObjectManager) {
        this.networkingApi = new NetworkingV1beta1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.endpointService = endpointService;
        this.serviceKindService = serviceKindService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<IngressTable> allNamespaceIngressTables() {
        ApiResponse<NetworkingV1beta1IngressTableList> apiResponse = networkingApi.searchIngressesTableList();
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            NetworkingV1beta1IngressTableList ingresses = apiResponse.getData();
            List<IngressTable> dataTable = ingresses.createDataTableList();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<IngressTable> ingresses(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceIngressTables();
        }
        ApiResponse<NetworkingV1beta1IngressTableList> apiResponse = networkingApi.searchIngressesTableList(namespace);
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            NetworkingV1beta1IngressTableList ingresses = apiResponse.getData();
            return ingresses.createDataTableList();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<IngressDescribe> ingress(String namespace, String name) {
        ApiResponse<NetworkingV1beta1Ingress> apiResponse = networkingApi.readNamespacedIngressWithHttpInfo(name, namespace, "true", Boolean.TRUE, Boolean.FALSE);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        IngressDescribe.IngressDescribeBuilder builder = IngressDescribe.builder();
        NetworkingV1beta1Ingress data = apiResponse.getData();
        setIngress(builder, data);

        IngressDescribe ingressDescribe = builder.build();

        setIngressRules(ingressDescribe);


        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("Ingress",
            ingressDescribe.getNamespace(), ingressDescribe.getName(), ingressDescribe.getUid());
        eventTableListOptional.ifPresent(v1EventTableList -> ingressDescribe.setEvents(v1EventTableList.createDataTableList()));

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

    private void setIngressRules(IngressDescribe ingressDescribe) {
        if (CollectionUtils.isEmpty(ingressDescribe.getRules())) {
            return;
        }

        List<IngressDescribe.IngressDescribeRule> ingressDescribeRules = ingressDescribe.getRules().stream()
            .map(rule -> makeRule(ingressDescribe.getNamespace(), rule))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        ingressDescribe.setDescribeRules(ingressDescribeRules);
    }

    private List<IngressDescribe.IngressDescribeRule> makeRule(String namespace, NetworkingV1beta1IngressRule rule) {
        if (rule.getHttp() == null) {
            return Collections.emptyList();
        }

        return rule.getHttp().getPaths().stream()
            .map(path -> IngressDescribe.IngressDescribeRule.builder()
                .host(rule.getHost())
                .path(path.getPath())
                .backend(path.getBackend().getServiceName() + ":" + path.getBackend().getServicePort())
                .endpoints(getEndpoints(namespace, path.getBackend()))
                .build()
            ).collect(Collectors.toList());
    }

    private List<String> getEndpoints(String namespace, NetworkingV1beta1IngressBackend backend) {
        String serviceName = backend.getServiceName();
        IntOrString servicePort = backend.getServicePort();

        Optional<EndpointDescribe> endpointOptional = endpointService.endpointWithoutEvent(namespace, serviceName);
        if (!endpointOptional.isPresent()) {
            return Collections.emptyList();
        }

        ServiceDescribe serviceDescribe = serviceKindService.serviceWithoutEvents(namespace, serviceName).orElse(null);

        if (serviceDescribe == null || CollectionUtils.isEmpty(serviceDescribe.getPorts())) {
            return Collections.emptyList();
        }

        String servicePortName = serviceDescribe.getPorts().stream()
            .filter(v1ServicePort -> (servicePort.isInteger() && servicePort.getIntValue().equals(v1ServicePort.getPort()))
                || (!servicePort.isInteger() && StringUtils.equalsAnyIgnoreCase(servicePort.getStrValue(), v1ServicePort.getName()))).map(v1ServicePort -> {
                if (v1ServicePort.getName() != null) {
                    return v1ServicePort.getName();
                }
                return v1ServicePort.getPort().toString();
            }).findFirst().orElse("");

        EndpointDescribe endpointDescribe = endpointOptional.get();
        List<V1EndpointSubset> subsets = endpointDescribe.getSubsets();

        if (CollectionUtils.isEmpty(subsets)) {
            return Collections.emptyList();
        }

        return subsets.stream()
            .filter(subset -> subset.getAddresses() != null)
            .map(subset -> {
                if (CollectionUtils.isEmpty(subset.getPorts())) {
                    return subset.getAddresses().stream()
                        .map(V1EndpointAddress::getIp)
                        .collect(Collectors.toList());
                } else {
                    return subset.getPorts().stream()
                        .filter(port -> StringUtils.equalsIgnoreCase(port.getName(), servicePortName))
                        .map(port -> subset.getAddresses().stream()
                            .map(address -> address.getIp() + ":" + port.getPort())
                            .collect(Collectors.toList())
                        ).flatMap(Collection::stream)
                        .collect(Collectors.toList());
                }
            }).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
