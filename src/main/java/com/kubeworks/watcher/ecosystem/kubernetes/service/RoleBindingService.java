package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingTable;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;

public interface RoleBindingService {

    @SneakyThrows
    List<RoleBindingTable> allNamespaceRoleBindingTables();

    Optional<RoleBindingDescribe> roleBinding(String namespace, String name);
}
