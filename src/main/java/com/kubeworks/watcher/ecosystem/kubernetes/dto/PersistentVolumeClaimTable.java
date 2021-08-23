package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class PersistentVolumeClaimTable implements NamespaceSettable {

    String name;
    String namespace;
    String status;
    String volume;
    String capacity;
    String accessModes;
    String storageClass;
    String age;
    String volumeMode;
}
