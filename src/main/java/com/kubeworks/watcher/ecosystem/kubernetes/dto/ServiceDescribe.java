package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V1ServicePort;
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
public class ServiceDescribe {

    @Builder
    private ServiceDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                               Map<String, String> labels, Map<String, String> annotations,
                               Map<String, String> selector, String clusterIp,
                               List<V1ServicePort> ports, List<EndpointTable> endpoints, String type,
                               List<EventTable> events) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.clusterIp = clusterIp;
        this.type = type;
        this.ports = ports;
        this.selector = selector;
        this.endpoints = endpoints;
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
    String clusterIp;
    String type;
    List<V1ServicePort> ports;

    List<EndpointTable> endpoints;
    List<EventTable> events;

}
