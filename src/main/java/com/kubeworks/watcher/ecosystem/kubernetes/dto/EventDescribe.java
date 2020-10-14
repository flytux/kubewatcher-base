package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.openapi.models.V1EventSource;
import io.kubernetes.client.openapi.models.V1ObjectReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDescribe {

    @Builder
    private EventDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                          String type, String reason, String message, int count, V1EventSource source,
                          DateTime firstTimestamp, DateTime lastTimestamp, V1ObjectReference involvedObject) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.type = type;
        this.reason = reason;
        this.message = message;
        this.count = count;
        this.source = source;
        this.firstTimestamp = firstTimestamp;
        this.lastTimestamp = lastTimestamp;
        this.involvedObject = involvedObject;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    String type;
    String reason;
    String message;
    int count;

    V1EventSource source;

    DateTime firstTimestamp;
    DateTime lastTimestamp;

    V1ObjectReference involvedObject;

}
