package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NamespaceDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NamespaceTable;

import java.util.List;
import java.util.Optional;

public interface NamespaceService {

    List<NamespaceTable> allNamespaceTables();

    Optional<NamespaceDescribe> namespace(String name);

}
