package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ServiceTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EndpointService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ServiceKindService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.openapi.models.V1ServiceStatus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ServiceKindServiceImpl implements ServiceKindService {

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreApi;
    private final EndpointService endpointService;
    private final EventService eventService;
    private final K8sObjectManager k8sObjectManager;

    public ServiceKindServiceImpl(ApiClient k8sApiClient, EndpointService endpointService, EventService eventService,
                                  K8sObjectManager k8sObjectManager){
        this.k8sApiClient = k8sApiClient;
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
        this.endpointService = endpointService;
        this.eventService = eventService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<ServiceTable> allNamespaceServiceTables() {
        ApiResponse<V1ServiceTableList> apiResponse = coreApi.allNamespaceServiceAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ServiceTableList services = apiResponse.getData();
            List<ServiceTable> dataTable = services.getDataTable();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<ServiceTable> services(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceServiceTables();
        }
        ApiResponse<V1ServiceTableList> apiResponse = coreApi.namespaceServiceAsTables(namespace,"true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            V1ServiceTableList services = apiResponse.getData();
            return services.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<ServiceDescribe> serviceWithoutEvents(String namespace, String name) {
        ApiResponse<V1Service> apiResponse = coreApi.readNamespacedServiceWithHttpInfo(name, namespace, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        ServiceDescribe.ServiceDescribeBuilder builder = ServiceDescribe.builder();
        V1Service data = apiResponse.getData();
        setService(builder, data);

        ServiceDescribe serviceDescribe = builder.build();

        return Optional.of(serviceDescribe);
    }

    @SneakyThrows
    @Override
    public Optional<ServiceDescribe> service(String namespace, String name) {

        Optional<ServiceDescribe> serviceDescribeOptional = serviceWithoutEvents(namespace, name);
        serviceDescribeOptional.ifPresent(serviceDescribe -> {
            List<EndpointTable> endpoint = endpointService.endpointTable(serviceDescribe.getNamespace(),
                serviceDescribe.getName());

            serviceDescribe.setEndpoints(endpoint);

            Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("Service",
                serviceDescribe.getNamespace(), serviceDescribe.getName(), serviceDescribe.getUid());
            eventTableListOptional.ifPresent(v1EventTableList -> serviceDescribe.setEvents(v1EventTableList.getDataTable()));
        });

        return serviceDescribeOptional;
    }

    private void setService(ServiceDescribe.ServiceDescribeBuilder builder, V1Service data) {
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
            V1ServiceSpec spec = data.getSpec();
            builder.clusterIp(spec.getClusterIP());
            builder.type(spec.getType());
            builder.ports(spec.getPorts());
            builder.selector(spec.getSelector());
        }

        if (data.getStatus() != null) {
            V1ServiceStatus status = data.getStatus();
        }
    }

}
