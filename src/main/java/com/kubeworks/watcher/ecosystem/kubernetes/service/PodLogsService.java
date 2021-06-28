package com.kubeworks.watcher.ecosystem.kubernetes.service;

import java.util.Map;

public interface PodLogsService {
    Map<String, String> searchPodLog(final String namespace, final String name, final String container, final String sinceTime);
}
