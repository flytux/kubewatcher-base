package com.kubeworks.watcher.preference.service;

import com.kubeworks.watcher.config.properties.ApplicationServiceProperties;

public interface ManagementService {

    ApplicationServiceProperties.Service managementService(String name);

    ApplicationServiceProperties.Service managementService(String namespace, String name);

    ApplicationServiceProperties.Service managementServiceWithDefault(String namespace, String name);


}
