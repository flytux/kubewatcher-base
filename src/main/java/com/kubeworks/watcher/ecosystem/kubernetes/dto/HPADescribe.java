package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V2beta2HorizontalPodAutoscalerCondition;
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
public class HPADescribe {

    @Builder
    private HPADescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                        Map<String, String> labels, Map<String, String> annotations,
                        String reference, Integer minReplicas, Integer maxReplicas,
                        Integer currentReplicas, Integer desiredReplicas,
                        Map<String, List<HPAMetric>> metrics, DateTime lastScaleTime,
                        List<V2beta2HorizontalPodAutoscalerCondition> conditions, List<EventTable> events) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.reference = reference;
        this.minReplicas = minReplicas;
        this.maxReplicas = maxReplicas;
        this.metrics = metrics;
        this.lastScaleTime = lastScaleTime;
        this.currentReplicas = currentReplicas;
        this.desiredReplicas = desiredReplicas;
        this.conditions = conditions;
        this.events = events;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;

    String reference;

    int minReplicas;
    int maxReplicas;
    Map<String, List<HPAMetric>> metrics;

    int currentReplicas;
    int desiredReplicas;
    DateTime lastScaleTime;

    List<V2beta2HorizontalPodAutoscalerCondition> conditions;

    List<EventTable> events;
}
