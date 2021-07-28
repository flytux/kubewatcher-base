package com.kubeworks.watcher.data.vo;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum AlertResource implements AbstractEnum<String> {

    CPU("CPU"),
    MEMORY("MEMORY"),
    DISK("DISK"),
    STRING("STRING");

    private final String value;

    AlertResource(String value) {
        this.value = value;
    }

    public static List<AlertResource> getAlertResourcesByMetric() {
        return ImmutableList.of(AlertResource.CPU, AlertResource.MEMORY, AlertResource.DISK);
    }

    public static List<AlertResource> getAlertResourcesByLog() {
        return ImmutableList.of(AlertResource.STRING);
    }

    @Override
    public String getValue() {
        return value;
    }
}
