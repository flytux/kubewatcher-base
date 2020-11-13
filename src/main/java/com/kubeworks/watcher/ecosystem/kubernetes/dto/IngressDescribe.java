package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.NetworkingV1beta1IngressRule;
import io.kubernetes.client.openapi.models.V1LoadBalancerStatus;
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
public class IngressDescribe {

    @Builder
    private IngressDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                            Map<String, String> annotations, List<NetworkingV1beta1IngressRule> rules,
                            List<EventTable> events, List<EndpointTable> endpoints, V1LoadBalancerStatus loadBalancer) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.annotations = annotations;
        this.rules = rules;
        this.events = events;
        this.endpoints = endpoints;
        this.loadBalancer = loadBalancer;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> annotations;

    List<NetworkingV1beta1IngressRule> rules;
    List<EventTable> events;
    List<EndpointTable> endpoints;

    V1LoadBalancerStatus loadBalancer;



}
