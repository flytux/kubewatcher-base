package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinitionCondition;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinitionNames;
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
public class CustomResourceDescribe {

    @Builder
    private CustomResourceDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                                   Map<String, String> labels, Map<String, String> annotations,
                                   String group, List<String> versions, String scope, String conversion, String webhookYaml,
                                   List<V1CustomResourceDefinitionCondition> conditions, V1CustomResourceDefinitionNames acceptedNames,
                                   List<EventTable> events) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.group = group;
        this.versions = versions;
        this.scope = scope;
        this.conversion = conversion;
        this.webhookYaml = webhookYaml;
        this.conditions = conditions;
        this.acceptedNames = acceptedNames;
        this.events = events;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;

    String group;
    List<String> versions;
    String scope;
    String conversion;
    String webhookYaml;

    List<V1CustomResourceDefinitionCondition> conditions;

    V1CustomResourceDefinitionNames acceptedNames;

    List<EventTable> events;

}
