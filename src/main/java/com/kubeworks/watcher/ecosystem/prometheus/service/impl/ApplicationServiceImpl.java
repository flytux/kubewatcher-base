package com.kubeworks.watcher.ecosystem.prometheus.service.impl;

import com.kubeworks.watcher.data.entity.ApplicationManagement;
import com.kubeworks.watcher.data.repository.ApplicationRepository;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service(value="applicationService")
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository repo;
    private final ApplicationManagementHandler handler;
    private final ApplicationManagement unknownBoard;

    @Autowired
    public ApplicationServiceImpl(final ApplicationRepository repo, final ApplicationManagementHandler handler) {

        this.repo = repo;
        this.handler = handler;
        this.unknownBoard = createUnknown();
    }

    @Override
    public List<ApplicationManagement> getApplicationManagementList() {
        return repo.findAll();
    }

    @Override
    public String getServiceNamesOfPromQL() {
        return handler.retrieve().stream().map(ApplicationManagement::getName).collect(Collectors.joining(".*|", "", ".*"));
    }

    @Override
    public String getServiceNamesLoki() {
        return handler.retrieve().stream().map(ApplicationManagement::getName).collect(Collectors.joining("|"));
    }

    @Override
    public List<String> getNamespaces() {
        return handler.retrieve().stream().map(ApplicationManagement::getNamespace).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<String, List<ApplicationManagement>> getManagementByNamespace() {
        return handler.retrieve().stream().collect(Collectors.groupingBy(ApplicationManagement::getNamespace));
    }

    @Override
    public List<String> getManagementByName() {
        return handler.retrieve().stream().map(ApplicationManagement::getName).collect(Collectors.toList());
    }

    @Override
    public ApplicationManagement getUnknownBoard() {
        return this.unknownBoard;
    }

    @Override
    public String getDisplayName(final String name) {
        return handler.retrieve().stream().filter(s -> StringUtils.equalsIgnoreCase(name, s.getName())).map(ApplicationManagement::getDisplayName).findFirst().orElse("Unknown");
    }

    @Override
    public Map<String, ApplicationManagement> getServiceMap() {
        return handler.retrieve().stream().collect(Collectors.toConcurrentMap(ApplicationManagement::getName, Function.identity()));
    }

    private ApplicationManagement createUnknown() {

        final ApplicationManagement unknown = new ApplicationManagement();
        unknown.setName(ExternalConstants.UNKNOWN);
        unknown.setDisplayName(ExternalConstants.UNKNOWN_DASH);

        return unknown;
    }
}
