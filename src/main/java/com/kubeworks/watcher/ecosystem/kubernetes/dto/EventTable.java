package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class EventTable {

    @Builder
    private EventTable(String lastSeen, String type, String reason, String object,
                       String subObject, String source, String message, String firstSeen,
                       String count, String name, String namespace) {
        this.lastSeen = lastSeen;
        this.type = type;
        this.reason = reason;
        this.object = object;
        this.subObject = subObject;
        this.source = source;
        this.message = message;
        this.firstSeen = firstSeen;
        this.count = count;
        this.name = name;
        this.namespace = namespace;
    }

    String lastSeen;
    String type;
    String reason;
    String object;
    String subObject;
    String source;
    String message;
    String firstSeen;
    String count;
    String name;
    String namespace;

}
