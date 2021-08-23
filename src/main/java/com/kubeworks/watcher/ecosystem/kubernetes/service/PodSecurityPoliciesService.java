package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodSecurityPolicyDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodSecurityPolicyTable;

import java.util.List;
import java.util.Optional;

public interface PodSecurityPoliciesService {

    List<PodSecurityPolicyTable> allNamespacePodSecurityPolicyTables();
    Optional<PodSecurityPolicyDescribe> podSecurityPolicy(String name);
}
