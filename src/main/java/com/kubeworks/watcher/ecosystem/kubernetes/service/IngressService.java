package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.IngressDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.IngressTable;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;

public interface IngressService {

    List<IngressTable> allNamespaceIngressTables();

    Optional<IngressDescribe> ingress(String namespace, String name);

}
