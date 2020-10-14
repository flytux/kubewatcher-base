package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1ObjectColumnDefinition {

    public static final String SERIALIZED_NAME = "name";
    @SerializedName(SERIALIZED_NAME)
    String name;

    public static final String SERIALIZED_TYPE = "type";
    @SerializedName(SERIALIZED_TYPE)
    String type;

    public static final String SERIALIZED_FORMAT = "format";
    @SerializedName(SERIALIZED_FORMAT)
    String format;

    public static final String SERIALIZED_DESCRIPTION = "description";
    @SerializedName(SERIALIZED_DESCRIPTION)
    String description;

    public static final String SERIALIZED_PRIORITY = "priority";
    @SerializedName(SERIALIZED_PRIORITY)
    int priority;

}
