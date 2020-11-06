package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyTable;
import lombok.SneakyThrows;

import java.util.List;

public interface NetworkPolicyService {

    @SneakyThrows
    List<NetworkPolicyTable> allNamespaceNetworkPolicyTables();
}
