package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.K8sObjectManager;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.NetworkingV1NetworkPolicyTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.NetworkingV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.NetworkPolicyService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NetworkPolicyServiceImpl implements NetworkPolicyService {

    private static final String POD = "Pod";
    private static final String EGRESS = "egress";
    private static final String INGRESS = "ingress";
    private static final String NAMESPACE = "NameSpace";

    private static final String EGRESS_POD_STR = EGRESS + POD;
    private static final String INGRESS_POD_STR = INGRESS + POD;
    private static final String EGRESS_NAMESPACE_STR = EGRESS + NAMESPACE;
    private static final String INGRESS_NAMESPACE_STR = INGRESS + NAMESPACE;

    private final K8sObjectManager manager;
    private final NetworkingV1ApiExtendHandler handler;

    @Autowired
    public NetworkPolicyServiceImpl(final K8sObjectManager manager, final ApiClient client) {
        this.manager = manager;
        this.handler = new NetworkingV1ApiExtendHandler(client);
    }

    @Override
    @SneakyThrows
    public List<NetworkPolicyTable> allNamespaceNetworkPolicyTables() {

        final ApiResponse<NetworkingV1NetworkPolicyTableList> response = handler.searchNetworkPoliciesTableList();

        if (ExternalConstants.isSuccessful(response.getStatusCode())) {
            final List<NetworkPolicyTable> dataTable = response.getData().createDataTableList();
            dataTable.sort((o, n) -> manager.compareByNamespace(o.getNamespace(), n.getNamespace()));

            return dataTable;
        }

        return Collections.emptyList();
    }

    @Override
    @SneakyThrows
    public List<NetworkPolicyTable> policies(final String namespace) {

        if (StringUtils.isBlank(namespace) || StringUtils.equalsIgnoreCase(namespace, "all")) {
            return allNamespaceNetworkPolicyTables();
        }

        final ApiResponse<NetworkingV1NetworkPolicyTableList> response = handler.searchNetworkPoliciesTableList(namespace);

        if (ExternalConstants.isSuccessful(response.getStatusCode())) {
            return response.getData().createDataTableList();
        }

        return Collections.emptyList();
    }

    @Override
    @SneakyThrows
    public Optional<NetworkPolicyDescribe> networkPolicy(final String namespace, final String name) {

        final ApiResponse<V1NetworkPolicy> response = handler.readNamespacedNetworkPolicyWithHttpInfo(name, namespace, "true", Boolean.TRUE, Boolean.FALSE);

        if (!ExternalConstants.isSuccessful(response.getStatusCode())) {
            return Optional.empty();
        }

        final NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder = NetworkPolicyDescribe.builder();
        setService(builder, response.getData());

        return Optional.of(builder.build());
    }

    private void setService(final NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder, final V1NetworkPolicy data) {

        if (Objects.nonNull(data.getMetadata())) {
            final V1ObjectMeta metadata = data.getMetadata();

            builder.name(metadata.getName()).namespace(metadata.getNamespace()).labels(metadata.getLabels())
                .uid(metadata.getUid()).annotations(metadata.getAnnotations()).creationTimestamp(metadata.getCreationTimestamp());
        }

        if (Objects.nonNull(data.getSpec())) {
            final V1NetworkPolicySpec spec = data.getSpec();
            final List<V1NetworkPolicyIngressRule> ingresses = spec.getIngress();

            builder.egresses(spec.getEgress()).ingresses(ingresses);
            setSelector(builder, spec);

            Optional.ofNullable(ingresses).filter(CollectionUtils::isNotEmpty).ifPresent(e -> processIngresses(builder, e));
            Optional.ofNullable(spec.getEgress()).filter(CollectionUtils::isNotEmpty).ifPresent(e -> processEgresses(builder, e));
        }
    }

    private void processIngresses(final NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder, final List<V1NetworkPolicyIngressRule> ingresses) {

        for (final V1NetworkPolicyIngressRule rule : ingresses) {
            final List<V1NetworkPolicyPeer> fromPeer = rule.getFrom();

            if (CollectionUtils.isNotEmpty(fromPeer)) {
                for (final V1NetworkPolicyPeer peer : fromPeer) {
                    final V1LabelSelector pod = peer.getPodSelector();
                    final V1LabelSelector namespace = peer.getNamespaceSelector();

                    if (Objects.nonNull(pod)) {
                        setFromToSelector(builder, pod, INGRESS_POD_STR);
                    }
                    if (Objects.nonNull(namespace)) {
                        setFromToSelector(builder, namespace, INGRESS_NAMESPACE_STR);
                    }
                }
            }
        }
    }

    private void processEgresses(final NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder, final List<V1NetworkPolicyEgressRule> egresses) {

        for (final V1NetworkPolicyEgressRule egress : egresses) {
            final List<V1NetworkPolicyPeer> toPeer = egress.getTo();

            if (CollectionUtils.isNotEmpty(toPeer)) {
                for (final V1NetworkPolicyPeer to : toPeer) {
                    final V1LabelSelector pod = to.getPodSelector();
                    final V1LabelSelector namespace = to.getNamespaceSelector();

                    if (Objects.nonNull(pod)) {
                        setFromToSelector(builder, pod, EGRESS_POD_STR);
                    }
                    if (Objects.nonNull(namespace)) {
                        setFromToSelector(builder, namespace, EGRESS_NAMESPACE_STR);
                    }
                }
            }
        }
    }

    private void setSelector(final NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder, final V1NetworkPolicySpec spec) {

        final V1LabelSelector selector = spec.getPodSelector();

        if (Objects.nonNull(selector.getMatchLabels())) {
            builder.podSelector(selector.getMatchLabels());
        } else {
            final List<V1LabelSelectorRequirement> expressions = selector.getMatchExpressions();
            if (CollectionUtils.isNotEmpty(expressions)) {
                builder.podSelector(createPodSelector(expressions));
            }
        }
    }

    private Map<String, String> createPodSelector(final List<V1LabelSelectorRequirement> expressions) {
        return expressions.stream().filter(r -> Objects.nonNull(r.getValues()) && Objects.nonNull(r.getKey()))
            .collect(Collectors.toMap(V1LabelSelectorRequirement::getKey, v -> String.join(", ", v.getValues()) + "(" + v.getOperator() + ")"));
    }

    private void setFromToSelector(final NetworkPolicyDescribe.NetworkPolicyDescribeBuilder builder, final V1LabelSelector selector, final String select) {

        final Map<String, String> matchLabels = selector.getMatchLabels();
        if (Objects.nonNull(matchLabels)) {
            switch (select) {
                case INGRESS_POD_STR:
                    builder.ingressPod(matchLabels); break;
                case INGRESS_NAMESPACE_STR:
                    builder.ingressNamespace(matchLabels); break;
                case EGRESS_POD_STR:
                    builder.egressPod(matchLabels); break;
                case EGRESS_NAMESPACE_STR:
                    builder.egressNamespace(matchLabels); break;
                default: break;
            }
            return;
        }

        final List<V1LabelSelectorRequirement> expressions = selector.getMatchExpressions();
        if (CollectionUtils.isNotEmpty(expressions)) {
            final Map<String, String> selectMatchExpr = createPodSelector(expressions);
            switch (select) {
                case INGRESS_POD_STR:
                    builder.ingressPod(selectMatchExpr); break;
                case INGRESS_NAMESPACE_STR:
                    builder.ingressNamespace(selectMatchExpr); break;
                case EGRESS_POD_STR:
                    builder.egressPod(selectMatchExpr); break;
                case EGRESS_NAMESPACE_STR:
                    builder.egressNamespace(selectMatchExpr); break;
                default: break;
            }
        }
    }
}
