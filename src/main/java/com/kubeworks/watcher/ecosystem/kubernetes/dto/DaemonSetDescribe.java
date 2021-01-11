package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1DaemonSetCondition;
import io.kubernetes.client.openapi.models.V1Toleration;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DaemonSetDescribe {

    @Builder
    private DaemonSetDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                              Map<String, String> labels, Map<String, String> annotations, Map<String, String> selector,
                              String images, String strategy, List<V1Toleration> tolerations, String podStatus,
                              ObjectUsageResource resources, List<V1DaemonSetCondition> conditions,
                              List<PodTable> pods, List<EventTable> events, Map<String, String> templateLabels) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.selector = selector;
        this.images = images;
        this.strategy = strategy;
        this.tolerations = tolerations;
        this.podStatus = podStatus;
        this.resources = resources;
        this.conditions = conditions;
        this.pods = pods;
        this.events = events;
        this.templateLabels = templateLabels;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;
    Map<String, String> selector;

    String images;
    String strategy;
    List<V1Toleration> tolerations;

    String podStatus;
    ObjectUsageResource resources;

    List<V1DaemonSetCondition> conditions;

    List<PodTable> pods;
    List<EventTable> events;

    Map<String, String> templateLabels;

    public String getRequestResource(String name) {
        Quantity requestsMetric = resources.getRequestsMetric(name);
        return ExternalConstants.toStringQuantity(requestsMetric);
    }

    public String getLimitsMetric(String name) {
        Quantity limitsMetric = resources.getLimitsMetric(name);
        return ExternalConstants.toStringQuantity(limitsMetric);
    }

}
