package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import io.kubernetes.client.openapi.models.V1PodList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PodService {

    List<PodTable> allNamespacePodTables();

    List<PodTable> podTables(final String namespace);

    List<PodTable> podTables(final String namespace, final Map<String, String> selector);

    Optional<PodDescribe> pod(final String namespace, final String podName);

    Optional<V1PodList> nodePods(final String nodeName);

    List<String> containers(final String namespace, final String podName);

}
