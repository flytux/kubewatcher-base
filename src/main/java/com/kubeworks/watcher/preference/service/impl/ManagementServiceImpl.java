package com.kubeworks.watcher.preference.service.impl;

import com.kubeworks.watcher.data.entity.ApplicationManagement;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
import com.kubeworks.watcher.preference.service.ManagementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service(value="managementService")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ManagementServiceImpl implements ManagementService {

    private final ApplicationService applicationService;

    @Override
    public ApplicationManagement managementService(String name) {
        return applicationService.getApplicationManagementList().stream()
            .filter(service -> StringUtils.equalsIgnoreCase(service.getName(), name))
            .findFirst()
            .orElseGet(applicationService::getUnknownBoard);
    }

    @Override
    public ApplicationManagement managementService(String namespace, String name) {
        List<ApplicationManagement> services = applicationService.getManagementByNamespace().get(namespace);
        if (CollectionUtils.isEmpty(services)) {
            return applicationService.getUnknownBoard();
        }

        return services.stream()
            .filter(service -> StringUtils.startsWith(name, service.getName()))
            .findFirst()
            .orElseGet(applicationService::getUnknownBoard);
    }

    @Override
    public ApplicationManagement managementServiceWithDefault(String namespace, String name) {

        ApplicationManagement service = managementService(namespace, name);
        if (StringUtils.equalsIgnoreCase(service.getName(), ExternalConstants.UNKNOWN)) {
            ApplicationManagement selfDefault = new ApplicationManagement();
            selfDefault.setName(name);
            selfDefault.setDisplayName(name);
            return selfDefault;
        }
        return service;
    }
}
