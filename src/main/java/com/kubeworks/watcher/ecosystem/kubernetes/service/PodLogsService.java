package com.kubeworks.watcher.ecosystem.kubernetes.service;

import io.kubernetes.client.openapi.ApiException;

import java.util.Map;

public interface PodLogsService {

    Map<String, String> getPodLog(String podName, String namespace, String container, String sinceTime) throws ApiException;

}
