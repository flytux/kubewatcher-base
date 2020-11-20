package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V1PolicyRule;
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
public class RoleDescribe {

    @Builder
    private RoleDescribe(String name, String namespace, DateTime creationTimestamp, String uid, Map<String, String> labels,
                         Map<String,String> annotations, List<V1PolicyRule> rules, List<EventTable> events) {
        this.name = name;
        this.namespace = namespace;
        this.creationTimestamp = creationTimestamp;
        this.uid = uid;
        this.labels = labels;
        this.annotations = annotations;
        this.rules = rules;
        this.events = events;
    }

    String name;
    String namespace;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;
    String uid;
    Map<String, String> labels;
    Map<String, String> annotations;

    List<V1PolicyRule> rules;
    List<EventTable> events;

}
