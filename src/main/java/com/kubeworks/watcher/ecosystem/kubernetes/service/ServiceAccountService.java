package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceAccountTable;
import lombok.SneakyThrows;

import java.util.List;

public interface ServiceAccountService {

    @SneakyThrows
    List<ServiceAccountTable> allNamespaceServiceAccountTables();

}
