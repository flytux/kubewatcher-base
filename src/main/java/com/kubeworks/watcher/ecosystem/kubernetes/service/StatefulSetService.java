package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.StatefulSetDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.StatefulSetTable;

import java.util.List;
import java.util.Optional;

public interface StatefulSetService {

    List<StatefulSetTable> allNamespaceStatefulSetTables();

    List<StatefulSetTable> statefulSets(String namespace);

    Optional<StatefulSetDescribe> statefulSet(String namespace, String name);

}
