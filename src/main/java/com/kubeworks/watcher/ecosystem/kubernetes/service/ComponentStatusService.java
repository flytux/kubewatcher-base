package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.base.MetricResponseData;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ComponentStatusDescribe;

import java.util.Optional;

public interface ComponentStatusService {

    Optional<ComponentStatusDescribe> componentStatus(String name);

    ApiResponse<MetricResponseData> componentStatusMetric(String name);


}
