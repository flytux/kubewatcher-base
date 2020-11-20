package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointTable;
import lombok.SneakyThrows;

import java.util.List;

public interface EndpointService {

    @SneakyThrows
    List<EndpointTable> allNamespaceEndpointTables();

}
