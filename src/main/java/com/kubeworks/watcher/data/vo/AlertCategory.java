package com.kubeworks.watcher.data.vo;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum AlertCategory {

    NODE,
    POD,
    EVENT,
    LOG;

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
