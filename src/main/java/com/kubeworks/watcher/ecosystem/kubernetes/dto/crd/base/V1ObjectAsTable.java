package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1ObjectAsTable<O> {

    public static final String SERIALIZED_CELLS = "cells";
    @SerializedName(SERIALIZED_CELLS)
    List<String> cells;

    public static final String SERIALIZED_OBJECT = "object";
    @SerializedName(SERIALIZED_OBJECT)
    O object;

}
