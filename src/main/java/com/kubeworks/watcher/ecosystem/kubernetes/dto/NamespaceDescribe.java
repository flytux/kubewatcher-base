package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V1NamespaceCondition;
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
public class NamespaceDescribe {

    @Builder
    private NamespaceDescribe(String name, String uid, DateTime creationTimestamp,
                              Map<String, String> labels, Map<String, String> annotations,
                              List<String> finalizers, String status, List<V1NamespaceCondition> conditions,
                              List<LimitRangeDescribe> limitRanges, List<ResourceQuotaDescribe> resourceQuotas) {
        this.name = name;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.finalizers = finalizers;
        this.status = status;
        this.conditions = conditions;
        this.limitRanges = limitRanges;
        this.resourceQuotas = resourceQuotas;
    }

    String name;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;

    List<String> finalizers;

    String status;
    List<V1NamespaceCondition> conditions;

    List<LimitRangeDescribe> limitRanges;
    List<ResourceQuotaDescribe> resourceQuotas;


}
