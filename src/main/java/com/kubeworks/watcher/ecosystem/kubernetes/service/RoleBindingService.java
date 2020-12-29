package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingTable;

import java.util.List;
import java.util.Optional;

public interface RoleBindingService {

    List<RoleBindingTable> allNamespaceRoleBindingTables();

    List<RoleBindingTable> roleBindings(String namespace);

    Optional<RoleBindingDescribe> roleBinding(String namespace, String name);
}
