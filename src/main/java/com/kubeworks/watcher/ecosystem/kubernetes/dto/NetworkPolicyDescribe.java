package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NetworkPolicyDescribe {

    @Builder
    private NetworkPolicyDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                                  Map<String, String> labels, Map<String, String> annotations,
                                  V1NetworkPolicySpec specs, Map<String, String> podSelector,
                                  List<V1NetworkPolicyEgressRule> egresses, List<V1NetworkPolicyIngressRule> ingresses,
                                  Map<String, String> ingressNamespace, Map<String, String> ingressPod,
                                  Map<String, String> egressNamespace, Map<String, String> egressPod) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.specs = specs;
        this.egresses = egresses;
        this.ingresses = ingresses;
        this.podSelector = podSelector;
        this.ingressNamespace = ingressNamespace;
        this.ingressPod= ingressPod;
        this.egressNamespace = egressNamespace;
        this.egressPod= egressPod;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;

    V1NetworkPolicySpec specs;
    List<V1NetworkPolicyEgressRule> egresses;
    List<V1NetworkPolicyIngressRule> ingresses;
    Map<String, String> podSelector;

    Map<String, String> ingressNamespace;
    Map<String, String> ingressPod;

    Map<String, String> egressNamespace;
    Map<String, String> egressPod;


}
