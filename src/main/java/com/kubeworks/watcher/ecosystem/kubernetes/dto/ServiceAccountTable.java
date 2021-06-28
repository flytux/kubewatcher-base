package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ServiceAccountTable implements NamespaceSettable {

    String name;
    String namespace;
    String secrets;
    String age;
}
