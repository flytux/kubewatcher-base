package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NodeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NodeTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ObjectUsageResource;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1NodeTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.NodeService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodService;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NodeServiceImpl implements NodeService {

    public static final String RESOURCE_HUGE_PAGES_PREFIX = "hugepages-";

    public static final Quantity defaultQuantity = new Quantity("0");

    private final ApiClient k8sApiClient;
    private final CoreV1ApiExtendHandler coreApi;
    private final PodService podService;
    private final EventService eventService;

    public NodeServiceImpl(ApiClient k8sApiClient, PodService podService, EventService eventService) {
        this.k8sApiClient = k8sApiClient;
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
        this.podService = podService;
        this.eventService = eventService;
    }

    @SneakyThrows
    @Override
    public List<NodeTable> nodes() {
        ApiResponse<V1NodeTableList> response = coreApi.listNodeAsTable("true");
        V1NodeTableList v1ObjectTableList = response.getData();
        return v1ObjectTableList.getDataTable();
    }

    @SneakyThrows
    @Override
    public NodeTable node(String nodeName) {
        ApiResponse<V1NodeTableList> response = coreApi.readNodeAsTable(nodeName, "true");
        V1NodeTableList v1ObjectTableList = response.getData();
        Optional<NodeTable> nodeTableOptional = v1ObjectTableList.getDataTable().stream().findFirst();
        return nodeTableOptional.orElse(null);
    }

    @SneakyThrows
    @Override
    public NodeDescribe nodeDescribe(String nodeName) {

        // node
        ApiResponse<V1Node> nodeResponse = coreApi.readNodeWithHttpInfo(
            nodeName, "true", Boolean.TRUE, Boolean.FALSE);
        if (!ExternalConstants.isSuccessful(nodeResponse.getStatusCode())) {
            return null;
        }
        NodeDescribe.NodeDescribeBuilder nodeDescribeBuilder = NodeDescribe.builder();
        V1Node v1Node = nodeResponse.getData();
        setV1Node(nodeDescribeBuilder, v1Node);

        //pod
        Optional<V1PodList> v1PodListOptional = podService.nodePods(nodeName);


        if (v1PodListOptional.isPresent()) {
            V1PodList pods = v1PodListOptional.get();

            // node pod list
            List<ObjectUsageResource> objectUsageResources = pods.getItems().stream()
                .map(v1Pod -> getResource(v1Node, v1Pod)).collect(Collectors.toList());
            nodeDescribeBuilder.pods(objectUsageResources);

            // node resource usage
            Optional<ObjectUsageResource> allocatedResourcesOptional = objectUsageResources.stream()
                .reduce((firstResource, secondResource) -> {
                    Map<String, Quantity> firstRequests = firstResource.getRequests();
                    Map<String, Quantity> secondRequest = secondResource.getRequests();
                    if (secondRequest == null) {
                        return firstResource;
                    }
                    if (firstRequests == null) {
                        firstRequests = new HashMap<>(secondRequest.size());
                    }
                    computeResource(firstRequests, secondRequest, false);
                    return firstResource;
                });

            allocatedResourcesOptional.ifPresent(nodeDescribeBuilder::allocateResources);
        }

        NodeDescribe nodeDescribe = nodeDescribeBuilder.build();

        // event
//        Optional<V1EventTableList> eventTablesOptional = eventService.nodeEventTables(nodeName);
//        if (eventTablesOptional.isPresent()) {
//            V1EventTableList eventTableList = eventTablesOptional.get();
//            List<EventTable> events = eventTableList.getDataTable();
//            nodeDescribe.setEvents(events);
//        }

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("Node",
            "", nodeDescribe.getName(), nodeDescribe.getName());
        eventTableListOptional.ifPresent(v1EventTableList -> nodeDescribe.setEvents(v1EventTableList.getDataTable()));


        return nodeDescribe;
    }


    public void setV1Node(NodeDescribe.NodeDescribeBuilder builder, V1Node node) {
        if (node.getMetadata() != null) {
            builder.name(node.getMetadata().getName());
            builder.labels(node.getMetadata().getAnnotations());
            builder.creationTimestamp(node.getMetadata().getCreationTimestamp());

            if (node.getMetadata().getLabels() != null) {
                builder.roles(getRole(node.getMetadata().getLabels()));
                builder.labels(node.getMetadata().getLabels());
            }
        }

        if (node.getSpec() != null) {
            builder.taints(getTaintsKeyValue(node.getSpec().getTaints()));
            builder.unSchedulable(BooleanUtils.toBoolean(node.getSpec().getUnschedulable()));
            builder.podCIDR(node.getSpec().getPodCIDR());
            builder.podCIDRs(node.getSpec().getPodCIDRs());
        }

        if (node.getStatus() != null) {
            builder.conditions(node.getStatus().getConditions());
            builder.addresses(node.getStatus().getAddresses());
            builder.capacity(node.getStatus().getCapacity());
            builder.allocatable(node.getStatus().getAllocatable());
            builder.systemInfo(node.getStatus().getNodeInfo());
        }
    }

    private String getRole(Map<String, String> labels) {
        return labels.computeIfAbsent(ExternalConstants.KUBERNETES_IO_ROLE, key -> labels.keySet().stream()
            .filter(k -> StringUtils.startsWithIgnoreCase(k, ExternalConstants.NODE_ROLE_KUBERNETES_IO))
            .map(k -> StringUtils.trim(StringUtils.replace(k, "", "")))
            .filter(StringUtils::isNotEmpty)
            .findFirst().orElse(ExternalConstants.NONE));
    }

    private Map<String, String> getTaintsKeyValue(List<V1Taint> taints) {
        if (CollectionUtils.isEmpty(taints)) {
            return Collections.emptyMap();
        }
        return taints.stream().collect(Collectors.toMap(V1Taint::getKey, V1Taint::getKey, (o1, o2) -> o2));
    }

    private ObjectUsageResource getResource(V1Node v1Node, V1Pod pod) {

        List<V1Container> containers = pod.getSpec().getContainers();
        List<V1Container> initContainers = pod.getSpec().getInitContainers();
        if (CollectionUtils.isEmpty(containers) && CollectionUtils.isEmpty(initContainers)) {
            return null;
        }

        ObjectUsageResource objectUsageResource = new ObjectUsageResource();
        objectUsageResource.setNamespace(pod.getMetadata().getNamespace());
        objectUsageResource.setName(pod.getMetadata().getName());
        objectUsageResource.setStatus(pod.getStatus().getPhase());
        objectUsageResource.setCreationTimestamp(pod.getMetadata().getCreationTimestamp());

        computePodResource(objectUsageResource, containers, false);
        if (initContainers != null) {
            computePodResource(objectUsageResource, initContainers, true);
        }
        log.info("----> name={}, namespace={}, request={}, limit={}",
            objectUsageResource.getName(), objectUsageResource.getNamespace(),
            objectUsageResource.getRequests(), objectUsageResource.getLimits());

        return objectUsageResource;
    }

    private void computePodResource(ObjectUsageResource objectUsageResource, List<V1Container> containers, boolean isInitContainer) {
        containers.stream().map(V1Container::getResources).filter(Objects::nonNull)
            .forEach(resource -> {
                Map<String, Quantity> requests = resource.getRequests();
                if (requests != null) {
                    Map<String, Quantity> aggRequestResources = objectUsageResource.getRequests();
                    if (aggRequestResources == null) {
                        aggRequestResources = new HashMap<>();
                    }
                    computeResource(aggRequestResources, requests, isInitContainer);
                    objectUsageResource.setRequests(aggRequestResources);
                }
                Map<String, Quantity> limits = resource.getLimits();
                if (limits != null) {
                    Map<String, Quantity> aggLimitResources = objectUsageResource.getLimits();
                    if (aggLimitResources == null) {
                        aggLimitResources = new HashMap<>();
                    }
                    computeResource(aggLimitResources, limits, isInitContainer);
                    objectUsageResource.setLimits(aggLimitResources);
                }
            });
    }

    private void computeResource(Map<String, Quantity> aggResource, Map<String, Quantity> newResource, boolean isInitContainer) {
        newResource.forEach((resourceKey, resource) -> {
            Quantity aggQuantity = aggResource.getOrDefault(resourceKey, defaultQuantity);
            if (isInitContainer && resource.getNumber().compareTo(aggQuantity.getNumber()) > 0) {
                aggResource.put(resourceKey, resource);
                return;
            }
            BigDecimal add = aggQuantity.getNumber().add(resource.getNumber());
            aggResource.put(resourceKey, new Quantity(add, aggQuantity.getFormat()));
        });
    }


}