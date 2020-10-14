package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import io.kubernetes.client.openapi.models.V1PodList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PodService {

    List<PodTable> allNamespacePodTables();

    List<PodTable> podTables(String namespace, Map<String, String> selector);

    Optional<PodDescribe> pod(String namespace, String podName);

    Optional<V1PodList> nodePods(String nodeName);



}
