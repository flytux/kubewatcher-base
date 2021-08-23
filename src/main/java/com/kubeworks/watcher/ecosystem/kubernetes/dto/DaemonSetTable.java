package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class DaemonSetTable implements NamespaceSettable {

    String name;
    String namespace;
    String desired;
    String current;
    String ready;
    String upToDate;
    String available;
    String nodeSelector;
    String age;
    String containers;
    String images;
    String selector;
}
