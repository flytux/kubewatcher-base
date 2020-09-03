package com.kubeworks.watcher.data.vo;

public enum VariableType implements AbstractEnum<String> {

    METRIC_LABEL_VALUES("metric_label_values"),
    PROMQL("promql"),
    API("api");

    private final String value;

    VariableType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }



}
