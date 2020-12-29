package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleTable;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<RoleTable> allNamespaceRoleTables();

    List<RoleTable> roles(String namespace);

    Optional<RoleDescribe> role(String namespace, String name);
}
