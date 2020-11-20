package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.IngressTable;
import lombok.SneakyThrows;

import java.util.List;

public interface IngressService {

    @SneakyThrows
    List<IngressTable> allNamespaceIngressTables();

    //Optional<IngressDescribe> ingress(String namespace, String name);
}
