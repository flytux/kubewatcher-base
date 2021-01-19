package com.kubeworks.watcher.data.vo;

public enum QueryType implements AbstractEnum<String> {

    METRIC("METRIC"),
    PROXY_METRIC("PROXY_METRIC"),
    KUBERNETES("KUBERNETES"),
    API("API");

    private final String value;

    QueryType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }



}
