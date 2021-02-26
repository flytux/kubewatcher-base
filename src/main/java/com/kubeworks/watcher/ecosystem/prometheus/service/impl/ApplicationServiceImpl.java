package com.kubeworks.watcher.ecosystem.prometheus.service.impl;

import com.kubeworks.watcher.data.entity.ApplicationManagement;
import com.kubeworks.watcher.data.repository.ApplicationRepository;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    ApplicationManagement unknownBoard;
    List<ApplicationManagement> boardList;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository){
        this.applicationRepository = applicationRepository;

        this.boardList = applicationRepository.findAll();

        this.unknownBoard = new ApplicationManagement();
        this.unknownBoard.setName(ExternalConstants.UNKNOWN);
        this.unknownBoard.setDisplayName(ExternalConstants.UNKNOWN_DASH);
    }

    @Override
    public List<ApplicationManagement> getApplicationManagementList() {
        return applicationRepository.findAll();
    }

    public Map<String, ApplicationManagement> getServiceMap() {
        return boardList.stream().collect(Collectors.toConcurrentMap(ApplicationManagement::getName, service -> service));
    }

    @Override
    public String getServiceNamesOfPromQL() {
        return boardList.stream().map(ApplicationManagement::getName).collect(Collectors.joining(".*|","", ".*"));
    }

    public String getDisplayName(String name) {
        return boardList.stream()
            .filter(service -> StringUtils.equalsIgnoreCase(name, service.getName()))
            .map(ApplicationManagement::getDisplayName)
            .findFirst().orElse("Unknown");
    }

    @Override
    public List<String> getNamespaces(){
        return boardList.stream().map(ApplicationManagement::getNamespace).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<String, List<ApplicationManagement>> getBoardByNamespace(){
        return boardList.stream().collect(Collectors.groupingBy(ApplicationManagement::getNamespace));
    }

    @Override
    public ApplicationManagement getUnknownBoard(){
        return this.unknownBoard;
    }
}
