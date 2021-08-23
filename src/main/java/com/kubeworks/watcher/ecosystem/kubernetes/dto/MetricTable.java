package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class MetricTable implements NamespaceSettable {

    String name;
    String namespace;
    Quantity cpu;
    Quantity memory;
}
