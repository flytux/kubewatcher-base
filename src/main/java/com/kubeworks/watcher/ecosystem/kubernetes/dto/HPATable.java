package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class HPATable implements NamespaceSettable {

    String name;
    String namespace;
    String reference;
    String targets;
    String minPods;
    String maxPods;
    String replicas;
    String age;
}
