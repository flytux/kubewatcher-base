package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class PodTable implements NamespaceSettable {

    String name;
    String namespace;
    String ready;
    String status;
    String restarts;
    String age;
    String ip;
    String node;
    String nominatedNode;
    String readinessGates;
    Quantity cpu;
    Quantity memory;
}
