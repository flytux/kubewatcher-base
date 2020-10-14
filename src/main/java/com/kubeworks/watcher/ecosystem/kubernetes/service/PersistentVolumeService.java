package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PersistentVolumeClaimDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PersistentVolumeClaimTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PersistentVolumeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PersistentVolumeTable;

import java.util.List;
import java.util.Optional;

public interface PersistentVolumeService {

    List<PersistentVolumeTable> allPersistentVolumeTables();

    Optional<PersistentVolumeDescribe> persistentVolume(String name);

    List<PersistentVolumeClaimTable> allNamespacePersistentVolumeClaimTables();

    Optional<PersistentVolumeClaimDescribe> persistentVolumeClaim(String namespace, String name);

}
