package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V1StatefulSetCondition;
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
public class StatefulSetDescribe {

    @Builder
    private StatefulSetDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                               Map<String, String> labels, Map<String, String> annotations, Map<String, String> selector,
                               String images, String strategy, List<V1Toleration> tolerations, int replicas, String podStatus,
                               ObjectUsageResource resources, List<V1StatefulSetCondition> conditions,
                               List<PodTable> pods, List<EventTable> events) {
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
        this.replicas = replicas;
        this.podStatus = podStatus;
        this.resources = resources;
        this.conditions = conditions;
        this.pods = pods;
        this.events = events;
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

    int replicas;
    String podStatus;
    ObjectUsageResource resources;

    List<V1StatefulSetCondition> conditions;

    List<PodTable> pods;
    List<EventTable> events;

}
