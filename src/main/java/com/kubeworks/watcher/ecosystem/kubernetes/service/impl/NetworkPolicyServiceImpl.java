package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1NetworkPolicyTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.NetworkingV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.NetworkPolicyService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NetworkPolicyServiceImpl implements NetworkPolicyService {

    private final ApiClient k8sApiClient;
    private final NetworkingV1ApiExtendHandler networkApi;
    private final EventService eventService;
    private final K8sObjectManager k8sObjectManager;

    public NetworkPolicyServiceImpl(ApiClient k8sApiClient, EventService eventService, K8sObjectManager k8sObjectManager) {
        this.k8sApiClient = k8sApiClient;
        this.networkApi = new NetworkingV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
        this.k8sObjectManager = k8sObjectManager;
    }

    @SneakyThrows
    @Override
    public List<NetworkPolicyTable> allNamespaceNetworkPolicyTables() {
        ApiResponse<NetworkingV1NetworkPolicyTableList> apiResponse = networkApi.allNamespaceNetworkPolicyAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            NetworkingV1NetworkPolicyTableList networkPolicies = apiResponse.getData();
            List<NetworkPolicyTable> dataTable = networkPolicies.getDataTable();
            dataTable.sort((o1, o2) -> k8sObjectManager.compareByNamespace(o1.getNamespace(), o2.getNamespace()));
            return dataTable;
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public List<NetworkPolicyTable> policies(String namespace) {
        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceNetworkPolicyTables();
        }
        ApiResponse<NetworkingV1NetworkPolicyTableList> apiResponse = networkApi.namespaceNetworkPolicyAsTables(namespace, "true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            NetworkingV1NetworkPolicyTableList networkPolicies = apiResponse.getData();
            return networkPolicies.getDataTable();
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

            builder.egresses(spec.getEgress());
            builder.ingresses(spec.getIngress());

            setSelector(builder, spec);

            // ingress
            List<V1NetworkPolicyIngressRule> ingresses = spec.getIngress();
            for (V1NetworkPolicyIngressRule ingress : ingresses) {
                List<V1NetworkPolicyPeer> fromList = ingress.getFrom();

                for (V1NetworkPolicyPeer from : fromList) {
                    V1LabelSelector ingressPod = from.getPodSelector();
                    V1LabelSelector ingressNameSpace = from.getNamespaceSelector();
                    if (ingressPod != null) {
                        setFromToSelector(builder, ingressPod, "ingressPod");
                    }
                    if (ingressNameSpace != null) {
                        setFromToSelector(builder, ingressNameSpace, "ingressNameSpace");
                    }
                }
            }

            // egress
            List<V1NetworkPolicyEgressRule> egresses = spec.getEgress();
            for (V1NetworkPolicyEgressRule egress : egresses) {
                List<V1NetworkPolicyPeer> toList = egress.getTo();

                for (V1NetworkPolicyPeer to : toList) {
                    V1LabelSelector egressPod = to.getPodSelector();
                    V1LabelSelector egressNameSpace = to.getNamespaceSelector();
                    if (egressPod != null) {
                        setFromToSelector(builder, egressPod, "egressPod");
                    }
                    if (egressNameSpace != null) {
                        setFromToSelector(builder, egressNameSpace, "egressNameSpace");
                    }
                }
            }
        }
    }

    private void setSelector(NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder, V1NetworkPolicySpec spec) {
        V1LabelSelector selector = spec.getPodSelector();

        if (selector.getMatchLabels() != null) {
            builder.podSelector(selector.getMatchLabels());
        } else {
            if (CollectionUtils.isNotEmpty(selector.getMatchExpressions())) {
                Map<String, String> selectMatchExpr = selector.getMatchExpressions().stream()
                    .filter(requirement -> Objects.nonNull(requirement.getValues()) && Objects.nonNull(requirement.getKey()))
                    .collect(Collectors.toMap(V1LabelSelectorRequirement::getKey, requirement -> {
                        String values = String.join(", ", requirement.getValues());
                        return values + "(" + requirement.getOperator() + ")";
                    }));
                builder.podSelector(selectMatchExpr);
            }
        }
    }

    private void setFromToSelector(NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder, V1LabelSelector selector, String select) {
        if (selector.getMatchLabels() != null) {
            switch (select) {
                case "ingressPod" :
                    builder.ingressPod(selector.getMatchLabels());
                    break;
                case "ingressNameSpace" :
                    builder.ingressNamespace(selector.getMatchLabels());
                    break;
                case "egressPod" :
                    builder.egressPod(selector.getMatchLabels());
                    break;
                case "egressNameSpace" :
                    builder.egressNamespace(selector.getMatchLabels());
                    break;
                default:
                    break;
            }
        } else {
            if (CollectionUtils.isNotEmpty(selector.getMatchExpressions())) {
                Map<String, String> selectMatchExpr = selector.getMatchExpressions().stream()
                    .filter(requirement -> Objects.nonNull(requirement.getValues()) && Objects.nonNull(requirement.getKey()))
                    .collect(Collectors.toMap(V1LabelSelectorRequirement::getKey, requirement -> {
                        String values = String.join(", ", requirement.getValues());
                        return values + "(" + requirement.getOperator() + ")";
                    }));

                switch (select) {
                    case "ingressPod" :
                        builder.ingressPod(selectMatchExpr);
                        break;
                    case "ingressNameSpace" :
                        builder.ingressNamespace(selectMatchExpr);
                        break;
                    case "egressPod" :
                        builder.egressPod(selectMatchExpr);
                        break;
                    case "egressNameSpace" :
                        builder.egressNamespace(selectMatchExpr);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
