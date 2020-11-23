package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V1RoleRef;
import io.kubernetes.client.openapi.models.V1Subject;
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
public class RoleBindingDescribe {

    @Builder
    private RoleBindingDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                                Map<String, String> labels, Map<String, String> annotations,
                                V1RoleRef roleRefs, List<V1Subject> subjects, List<EventTable> events) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.events = events;
        this.roleRefs = roleRefs;
        this.subjects = subjects;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;

    V1RoleRef roleRefs;
    List<V1Subject> subjects;
    List<EventTable> events;

}
