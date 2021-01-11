package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class MetricTable {

    String name;
    String namespace;
    Quantity cpu;
    Quantity memory;

}
