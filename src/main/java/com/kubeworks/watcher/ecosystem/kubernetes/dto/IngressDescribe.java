package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.NetworkingV1beta1IngressRule;
import io.kubernetes.client.openapi.models.V1LoadBalancerStatus;
import lombok.*;
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
                            List<EventTable> events, List<IngressDescribeRule> describeRules, V1LoadBalancerStatus loadBalancer) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.annotations = annotations;
        this.rules = rules;
        this.events = events;
        this.describeRules = describeRules;
        this.loadBalancer = loadBalancer;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> annotations;

    List<NetworkingV1beta1IngressRule> rules;
    List<IngressDescribeRule> describeRules;

    List<EventTable> events;

    V1LoadBalancerStatus loadBalancer;

    @Getter @Setter @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class IngressDescribeRule {

        @Builder
        private IngressDescribeRule(String host, String path, String backend, List<String> endpoints) {
            this.host = host;
            this.path = path;
            this.backend = backend;
            this.endpoints = endpoints;
        }

        String host;
        String path;
        String backend;
        List<String> endpoints;

    }
}


