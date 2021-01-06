package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V1DeploymentCondition;
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
public class DeploymentDescribe {

    @Builder
    private DeploymentDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                               Map<String, String> labels, Map<String, String> annotations, Integer replicas,
                               Map<String, String> selector, String strategy, List<V1DeploymentCondition> conditions,
                               ObjectUsageResource resources, List<PodTable> pods, List<EventTable> events,
                               Map<String, String> templateLabels) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.replicas = replicas;
        this.selector = selector;
        this.strategy = strategy;
        this.conditions = conditions;
        this.resources = resources;
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

    int replicas;
    Map<String, String> selector;
    String strategy;
    List<V1DeploymentCondition> conditions;

    ObjectUsageResource resources;

    List<PodTable> pods;
    List<EventTable> events;

    Map<String, String> templateLabels;


}
