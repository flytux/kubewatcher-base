package com.kubeworks.watcher.preference.service;

import com.kubeworks.watcher.data.entity.ApplicationManagement;

public interface ManagementService {

    ApplicationManagement managementService(String name);

    ApplicationManagement managementService(String namespace, String name);

    ApplicationManagement managementServiceWithDefault(String namespace, String name);
}
