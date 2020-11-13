package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodSecurityPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodSecurityPolicyDescribe;
import io.kubernetes.client.openapi.models.V1PodList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PodSecurityPoliciesService {

    List<PodSecurityPolicyTable> allNamespacePodSecurityPolicyTables();

    Optional<PodSecurityPolicyDescribe> podSecurityPolicy(String name);

}
