package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PodDescribeVolume {

    @Builder
    private PodDescribeVolume(String name, String type, List<String> values) {
        this.name = name;
        this.type = type;
        this.values = values;
    }

    String name;
    String type;
    List<String> values;

    public void addValue(String value) {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }
        this.values.add(value);
    }
}
