package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComponentStatusDescribe {

    @Builder
    private ComponentStatusDescribe(String name, boolean status, String message, String error) {
        this.name = name;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    String name;
    boolean status;
    String message;
    String error;
    List<EventTable> events;

}
