package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyTable;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;

public interface NetworkPolicyService {

    List<NetworkPolicyTable> allNamespaceNetworkPolicyTables();

    Optional<NetworkPolicyDescribe> networkPolicy(String namespace, String name);
}
