package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodSecurityPolicyDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodSecurityPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.RbacV1beta1PodSecurityPolicyTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.PolicyV1beta1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.PodSecurityPoliciesService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiResponse;
import io.kubernetes.client.openapi.models.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PodSecurityPoliciesServiceImpl implements PodSecurityPoliciesService {

    private final ApiClient k8sApiClient;
    private final PolicyV1beta1ApiExtendHandler policyPodSecurityV1beta1Api;

    public PodSecurityPoliciesServiceImpl(ApiClient k8sApiClient) {
        this.k8sApiClient = k8sApiClient;
        this.policyPodSecurityV1beta1Api = new PolicyV1beta1ApiExtendHandler(k8sApiClient);
    }

    @SneakyThrows
    @Override
    public List<PodSecurityPolicyTable> allNamespacePodSecurityPolicyTables() {
        ApiResponse<RbacV1beta1PodSecurityPolicyTableList> apiResponse = policyPodSecurityV1beta1Api.allNamespacePodSecurityPolicyAsTables("true");
        if (ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            RbacV1beta1PodSecurityPolicyTableList podSecurityPolicies = apiResponse.getData();
            return podSecurityPolicies.getDataTable();
        }
        return Collections.emptyList();
    }

    @SneakyThrows
    @Override
    public Optional<PodSecurityPolicyDescribe> podSecurityPolicy(String name) {
        ApiResponse<PolicyV1beta1PodSecurityPolicy> apiResponse = policyPodSecurityV1beta1Api.readPodSecurityPolicyWithHttpInfo(name, "true", true, false);
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return Optional.empty();
        }

        PodSecurityPolicyDescribe.PodSecurityPolicyDescribeBuilder builder = PodSecurityPolicyDescribe.builder();
        PolicyV1beta1PodSecurityPolicy data = apiResponse.getData();
        setPodSecurityPolicy(builder, data);

        PodSecurityPolicyDescribe roleDescribe = builder.build();

        return Optional.of(roleDescribe);
    }

    private void setPodSecurityPolicy(PodSecurityPolicyDescribe.PodSecurityPolicyDescribeBuilder builder, PolicyV1beta1PodSecurityPolicy data) {

        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
            builder.namespace(metadata.getNamespace());
            builder.uid(metadata.getUid());
            builder.labels(metadata.getLabels());
            builder.annotations(metadata.getAnnotations());
            builder.creationTimestamp(metadata.getCreationTimestamp());
        }

        if (data.getSpec() != null) {
            builder.specs(data.getSpec());
        }

    }
}
