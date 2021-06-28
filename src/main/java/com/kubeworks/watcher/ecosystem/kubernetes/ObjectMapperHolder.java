package com.kubeworks.watcher.ecosystem.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class ObjectMapperHolder {

    private static ObjectMapper mapper;
    private static final AtomicReference<Integer> REFERENCE = new AtomicReference<>();

    private ObjectMapperHolder() {
        throw new UnsupportedOperationException("Cannot instantiate this class");
    }

    public static ObjectMapper retrieveObjectMapper() {

        if (Objects.isNull(mapper)) {
            throw new IllegalArgumentException("ObjectMapper is null");
        }

        return mapper;
    }

    public static void putObjectMapper(final ObjectMapper source) {

        if (Objects.isNull(REFERENCE.get())) {
            mapper = source; REFERENCE.set(1); return;
        }

        throw new UnsupportedOperationException("This method can be called only once");
    }
}
