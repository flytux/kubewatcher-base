package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.DeploymentDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.DeploymentTable;

import java.util.List;
import java.util.Optional;

public interface DeploymentService {

    List<DeploymentTable> allNamespaceDeploymentTables();

    Optional<DeploymentDescribe> deployment(String namespace, String name);

}
