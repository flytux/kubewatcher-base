package com.kubeworks.watcher.data.vo;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum AlertResource {

    CPU,
    MEMORY,
    DISK,
    STRING;

    public static List<AlertResource> getAlertResourcesByMetric() {
        return ImmutableList.of(AlertResource.CPU, AlertResource.MEMORY, AlertResource.DISK);
    }

    public static List<AlertResource> getAlertResourcesByLog() {
        return ImmutableList.of(AlertResource.STRING);
    }
}
