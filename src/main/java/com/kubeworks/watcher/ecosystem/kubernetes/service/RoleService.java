package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleDescribe;

import java.util.List;
import java.util.Optional;

public interface RoleService {

    List<RoleTable> allNamespaceRoleTables();

    Optional<RoleDescribe> role(String namespace, String name);
}
