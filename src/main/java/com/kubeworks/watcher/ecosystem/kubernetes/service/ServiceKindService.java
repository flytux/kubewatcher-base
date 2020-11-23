package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceTable;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;

public interface ServiceKindService {

    @SneakyThrows
    List<ServiceTable> allNamespaceServiceTables();

    Optional<ServiceDescribe> service(String namespace, String name);

}
