package com.kubeworks.watcher.data.vo;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum AlertCategory implements AbstractEnum<String> {

    NODE("NODE"),
    POD("POD"),
    EVENT("EVENT"),
    LOG("LOG");

    private final String value;

    AlertCategory(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static List<AlertCategory> getAlertCategories(AlertType alertType) {
        switch (alertType) {
            case METRIC:
                return ImmutableList.of(AlertCategory.NODE, AlertCategory.POD);
            case LOG:
                return ImmutableList.of(AlertCategory.EVENT, AlertCategory.LOG);
            default:
                throw new IllegalArgumentException("Unsupported alertType=" + alertType);
        }
    }

}
