package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceAccountDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceAccountTable;

import java.util.List;
import java.util.Optional;

public interface ServiceAccountService {

    List<ServiceAccountTable> allNamespaceServiceAccountTables();

    List<ServiceAccountTable> serviceAccounts(String namespace);

    Optional<ServiceAccountDescribe> serviceAccount(String namespace, String name);
}
