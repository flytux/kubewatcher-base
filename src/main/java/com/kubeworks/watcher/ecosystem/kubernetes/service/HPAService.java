package com.kubeworks.watcher.ecosystem.kubernetes.service;


import com.kubeworks.watcher.ecosystem.kubernetes.dto.HPADescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.HPATable;

import java.util.List;
import java.util.Optional;

public interface HPAService {

    List<HPATable> allNamespaceHPATables();

    Optional<HPADescribe> hpa(String namespace, String name);

}
