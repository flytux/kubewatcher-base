package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.CustomResourceDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CustomResourceTable;

import java.util.List;
import java.util.Optional;

public interface CustomResourceService {

    List<CustomResourceTable> allCustomResourceTables();

    Optional<CustomResourceDescribe> customResource(String name);

}
