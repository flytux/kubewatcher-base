package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.base.MetricResponseData;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ComponentStatusDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ComponentStatusService;
import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.ApiregistrationV1Api;
import io.kubernetes.client.openapi.models.V1APIService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ComponentStatusServiceImpl implements ComponentStatusService {

    private final ApiregistrationV1Api api;

    public ComponentStatusServiceImpl(final ApiClient client) {
        this.api = new ApiregistrationV1Api(client);
    }

    @Override
    public Optional<ComponentStatusDescribe> componentStatus(String name) {
        return Optional.empty();
    }

    @Override
    public ApiResponse<MetricResponseData> componentStatusMetric(String name) {
        ApiResponse<MetricResponseData> response = new ApiResponse<>();
        try {
            V1APIService v1APIService = getComponentStatusDescribe(name);
            if (v1APIService != null) {
                List<Object> results = Arrays.asList(System.currentTimeMillis() / 1000, Boolean.parseBoolean(v1APIService.getStatus().getConditions().get(0).getStatus()) ? 1 : 0);
                response.setSuccess(true);
                response.setMessage("");
                response.setData(new MetricResponseData(Collections.singletonList(MetricResponseData.MetricResult.builder().value(results).build())));
            }
        } catch (Exception e) {
            log.error("failed componentStatuses metric={}", name, e);
            response.setSuccess(false);
        }
        return response;
    }

    @SneakyThrows
    private V1APIService getComponentStatusDescribe(String name) {

        final V1APIService v1APIService = api.patchAPIServiceStatus(name, new V1Patch(null), "true", null, null, null);

        if (Objects.isNull(v1APIService.getStatus()) || Objects.isNull(v1APIService.getStatus().getConditions())) { return null; }

        return v1APIService;
    }
}
