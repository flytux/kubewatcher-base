package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.StorageClassDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.StorageClassTable;

import java.util.List;
import java.util.Optional;

public interface StorageService {

    List<StorageClassTable> allStorageClassClaimTables();

    Optional<StorageClassDescribe> storageClass(String name);

}
