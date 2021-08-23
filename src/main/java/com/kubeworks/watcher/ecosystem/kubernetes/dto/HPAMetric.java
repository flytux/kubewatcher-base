package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kubernetes.client.openapi.models.V2beta2MetricIdentifier;
import io.kubernetes.client.openapi.models.V2beta2MetricTarget;
import io.kubernetes.client.openapi.models.V2beta2MetricValueStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HPAMetric {

    @Builder
    private HPAMetric(String type, String name,
                      V2beta2MetricIdentifier metric,
                      V2beta2MetricTarget target,
                      V2beta2MetricValueStatus status) {
        this.type = type;
        this.name = name;
        this.metric = metric;
        this.target = target;
        this.status = status;
    }

    String type; //ex) Resource, Object(Ingress/test), Pods
    String name;
    V2beta2MetricIdentifier metric;
    V2beta2MetricTarget target;
    V2beta2MetricValueStatus status;


    @JsonIgnore
    public List<String> getMetricSelector() {
        // TODO selector 공통 적용(class reflection 방식으로...)
        return Collections.emptyList();
    }
}
