package com.kubeworks.watcher.ecosystem.kubernetes.service.impl;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.base.MetricResponseData;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ComponentStatusDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.handler.CoreV1ApiExtendHandler;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ComponentStatusService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1ComponentCondition;
import io.kubernetes.client.openapi.models.V1ComponentStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ComponentStatusServiceImpl implements ComponentStatusService {

    private final CoreV1ApiExtendHandler coreApi;
    private final EventService eventService;

    @Autowired
    public ComponentStatusServiceImpl(ApiClient k8sApiClient, EventService eventService) {
        this.coreApi = new CoreV1ApiExtendHandler(k8sApiClient);
        this.eventService = eventService;
    }


    @Override
    public Optional<ComponentStatusDescribe> componentStatus(String name) {
        ComponentStatusDescribe componentStatusDescribe = getComponentStatusDescribe(name);
        if (componentStatusDescribe == null) {
            return Optional.empty();
        }

        Optional<V1EventTableList> eventTableListOptional = eventService.eventTable("ComponentStatus",
            null, componentStatusDescribe.getName(), null);
        eventTableListOptional.ifPresent(v1EventTableList -> componentStatusDescribe.setEvents(v1EventTableList.createDataTableList()));

        return Optional.empty();
    }

    @Override
    public ApiResponse<MetricResponseData> componentStatusMetric(String name) {
        ApiResponse<MetricResponseData> response = new ApiResponse<>();
        try {
            ComponentStatusDescribe componentStatusDescribe = getComponentStatusDescribe(name);
            if (componentStatusDescribe != null) {
                List<Object> results = Arrays.asList(System.currentTimeMillis() / 1000, componentStatusDescribe.isStatus() ? 1 : 0);
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
    private ComponentStatusDescribe getComponentStatusDescribe(String name) {
        io.kubernetes.client.openapi.ApiResponse<V1ComponentStatus> apiResponse = coreApi.readComponentStatusWithHttpInfo(name, "true");
        if (!ExternalConstants.isSuccessful(apiResponse.getStatusCode())) {
            return null;
        }

        ComponentStatusDescribe.ComponentStatusDescribeBuilder builder = ComponentStatusDescribe.builder();
        V1ComponentStatus data = apiResponse.getData();
        setComponentStatus(builder, data);

        return builder.build();
    }



    private void setComponentStatus(ComponentStatusDescribe.ComponentStatusDescribeBuilder builder, V1ComponentStatus data) {
        if (data.getMetadata() != null) {
            V1ObjectMeta metadata = data.getMetadata();
            builder.name(metadata.getName());
        }

        if (CollectionUtils.isNotEmpty(data.getConditions())) {
            List<V1ComponentCondition> conditions = data.getConditions();
            Optional<V1ComponentCondition> healthyOptional = conditions.stream()
                .filter(condition -> StringUtils.equalsIgnoreCase(condition.getType(), "Healthy"))
                .findFirst();
            if (healthyOptional.isPresent()) {
                V1ComponentCondition healthy = healthyOptional.get();
                builder.status(Boolean.parseBoolean(healthy.getStatus()));
                builder.message(healthy.getMessage());
                builder.error(healthy.getError());
            }
        }
        builder.message(ExternalConstants.UNKNOWN);
    }
}
