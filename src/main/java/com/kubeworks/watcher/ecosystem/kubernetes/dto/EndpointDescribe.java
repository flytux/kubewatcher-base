package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V1EndpointSubset;
import io.kubernetes.client.openapi.models.V1beta1EndpointPort;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointDescribe {

    @Builder
    private EndpointDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                             Map<String, String> labels, Map<String, String> annotations,
                             List<V1EndpointSubset> subsets, List<V1beta1EndpointPort> ports,
                             List<EventTable> events) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.subsets = subsets;
        this.ports = ports;
        this.events = events;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;

    @Builder.Default
    List<V1EndpointSubset> subsets = Collections.emptyList();
    List<V1beta1EndpointPort> ports;

    List<EventTable> events;
}
