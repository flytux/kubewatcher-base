package com.kubeworks.watcher.preference.service.impl;

import com.kubeworks.watcher.config.properties.ApplicationServiceProperties;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.preference.service.ManagementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("managementService")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ManagementServiceImpl implements ManagementService {

    private final ApplicationServiceProperties applicationServiceProperties;

    @Override
    public ApplicationServiceProperties.Service managementService(String name) {
        return applicationServiceProperties.getServices().stream()
            .filter(service -> StringUtils.equalsIgnoreCase(service.getName(), name))
            .findFirst()
            .orElse(applicationServiceProperties.getUnknownService());
    }

    @Override
    public ApplicationServiceProperties.Service managementService(String namespace, String name) {
        List<ApplicationServiceProperties.Service> services = applicationServiceProperties.getServiceByNamespace().get(namespace);
        if (CollectionUtils.isEmpty(services)) {
            return applicationServiceProperties.getUnknownService();
        }

        return services.stream()
            .filter(service -> StringUtils.startsWith(name, service.getName()))
            .findFirst()
            .orElse(applicationServiceProperties.getUnknownService());
    }

    @Override
    public ApplicationServiceProperties.Service managementServiceWithDefault(String namespace, String name) {

        ApplicationServiceProperties.Service service = managementService(namespace, name);
        if (StringUtils.equalsIgnoreCase(service.getName(), ExternalConstants.UNKNOWN)) {
            ApplicationServiceProperties.Service selfDefault = new ApplicationServiceProperties.Service();
            selfDefault.setName(name);
            selfDefault.setDisplayName(name);
            return selfDefault;
        }
        return service;
    }
}
