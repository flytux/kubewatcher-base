package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ServiceTable implements NamespaceSettable {

    String name;
    String namespace;
    String type;
    String clusterIp;
    String externalIp;
    String ports;
    String age;
    String selector;
}
