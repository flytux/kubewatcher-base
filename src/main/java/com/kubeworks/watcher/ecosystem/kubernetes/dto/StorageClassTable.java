package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class StorageClassTable {

    String name;
    String provisioner;
    String reclaimPolicy;
    String volumeBindingMode;
    String allowVolumeExpansion;
    String age;

}
