package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingTable;
import lombok.SneakyThrows;

import java.util.List;

public interface RoleBindingService {

    @SneakyThrows
    List<RoleBindingTable> allNamespaceRoleBindingTables();
}
