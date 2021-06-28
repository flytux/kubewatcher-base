package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class V1ObjectColumnDefinition {

    @SerializedName("name") String name;
    @SerializedName("type") String type;
    @SerializedName("format") String format;
    @SerializedName("description") String description;
    @SerializedName("priority") int priority;
}
