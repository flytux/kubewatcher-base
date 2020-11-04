package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1LabelSelector;
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
public class PersistentVolumeClaimDescribe {

    @Builder
    private PersistentVolumeClaimDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                                          Map<String, String> labels, Map<String, String> annotations,
                                          List<String> finalizers, List<String> accessModes, String storageClassName,
                                          ObjectUsageResource resources, Map<String, Quantity> capacity,
                                          V1LabelSelector selector, List<String> pods, String status,
                                          List<EventTable> events) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.finalizers = finalizers;
        this.accessModes = accessModes;
        this.storageClassName = storageClassName;
        this.resources = resources;
        this.capacity = capacity;
        this.selector = selector;
        this.pods = pods;
        this.status = status;
        this.events = events;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;

    List<String> finalizers;
    List<String> accessModes;
    String storageClassName;

    ObjectUsageResource resources;

    Map<String, Quantity> capacity;

    V1LabelSelector selector;

    List<String> pods;

    String status;
    List<EventTable> events;

}
