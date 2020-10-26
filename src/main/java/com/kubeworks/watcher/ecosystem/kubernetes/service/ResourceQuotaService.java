package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ResourceQuotaDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ResourceQuotaTable;

import java.util.List;
import java.util.Optional;

public interface ResourceQuotaService {

    List<ResourceQuotaTable> allNamespaceResourceQuotaTables();

    List<ResourceQuotaDescribe> listNamespacedResourceQuota(String namespace);

    Optional<ResourceQuotaDescribe> resourceQuota(String namespace, String name);

}
