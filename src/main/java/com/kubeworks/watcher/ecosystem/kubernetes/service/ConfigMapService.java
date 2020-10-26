package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ConfigMapDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ConfigMapTable;

import java.util.List;
import java.util.Optional;

public interface ConfigMapService {

    List<ConfigMapTable> allNamespaceConfigMapTables();

    Optional<ConfigMapDescribe> configMap(String namespace, String name);

}
