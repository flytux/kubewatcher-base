package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class StorageClassTable implements NamespaceSettable {

    String name;
    String provisioner;
    String reclaimPolicy;
    String volumeBindingMode;
    String allowVolumeExpansion;
    String age;

    @Override
    public void setNamespace(final String namespace) {
        // Do nothing
    }
}
