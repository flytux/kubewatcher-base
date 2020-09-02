package com.kubeworks.watcher.data.vo;

public enum PageRowType implements AbstractEnum<String>{

    PANEL("P"),
    VARIABLE("V"),
    DIVIDER("D");

    private final String value;

    PageRowType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
