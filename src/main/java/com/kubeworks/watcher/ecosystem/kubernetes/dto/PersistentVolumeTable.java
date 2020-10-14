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
public class PersistentVolumeTable {

    String name;
    String capacity;
    String accessModes;
    String reclaimPolicy;
    String status;
    String claim;
    String storageClass;
    String reason;
    String age;
    String volumeMode;

}
