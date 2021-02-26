package com.kubeworks.watcher.ecosystem.prometheus.service;

import com.kubeworks.watcher.data.entity.ApplicationManagement;

import java.util.List;
import java.util.Map;

public interface ApplicationService {

    List<ApplicationManagement> getApplicationManagementList();

    String getServiceNamesOfPromQL();

     List<String> getNamespaces();

    Map<String, List<ApplicationManagement>> getBoardByNamespace();

    ApplicationManagement getUnknownBoard();

}
