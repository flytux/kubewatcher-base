package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.V1NodeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1CellTableList;

public interface KubernetesClientService {

    V1CellTableList nodesTable();

    V1NodeDescribe describeNode(String nodeName);

}
