package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class ResourceQuotaTable implements NamespaceSettable {

    String name;
    String namespace;
    String age;
    String request;
    String limit;
}
