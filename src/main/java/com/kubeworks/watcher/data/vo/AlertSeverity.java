package com.kubeworks.watcher.data.vo;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum AlertSeverity implements AbstractEnum<String> {

    NONE(""),
    INFO("Info"),
    WARN("Warning"),
    DANGER("Danger");

    private final String value;

    AlertSeverity(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static List<AlertSeverity> getAlertSeverity() {
        return ImmutableList.of(AlertSeverity.INFO, AlertSeverity.WARN, AlertSeverity.DANGER);
    }
}
