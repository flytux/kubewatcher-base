package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceTable;

import java.util.List;
import java.util.Optional;

public interface ServiceKindService {

    List<ServiceTable> allNamespaceServiceTables();

    List<ServiceTable> services(String namespace);

    Optional<ServiceDescribe> serviceWithoutEvents(String namespace, String name);

    Optional<ServiceDescribe> service(String namespace, String name);
}
