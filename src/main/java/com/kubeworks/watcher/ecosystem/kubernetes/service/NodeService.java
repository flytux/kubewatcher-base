package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NodeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NodeTable;

import java.util.List;

public interface NodeService {

    List<NodeTable> nodes();

    NodeTable node(String nodeName);

    NodeDescribe nodeDescribe(String nodeName);

}
