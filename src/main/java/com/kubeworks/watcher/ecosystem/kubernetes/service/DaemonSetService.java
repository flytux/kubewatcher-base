package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.DaemonSetDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.DaemonSetTable;

import java.util.List;
import java.util.Optional;

public interface DaemonSetService {

    List<DaemonSetTable> allNamespaceDaemonSetTables();

    Optional<DaemonSetDescribe> daemonSet(String namespace, String name);

}
