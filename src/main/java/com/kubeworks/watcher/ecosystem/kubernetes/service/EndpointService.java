package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointTable;

import java.util.List;
import java.util.Optional;

public interface EndpointService {

    List<EndpointTable> allNamespaceEndpointTables();

    List<EndpointTable> endpoints(String namespace);

    List<EndpointTable> endpointTable(String namespace, String name);

    Optional<EndpointDescribe> endpointWithoutEvent(String namespace, String name);

    Optional<EndpointDescribe> endpoint(String namespace, String name);
}
