package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PersistentVolumeClaimTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;

public class V1PersistentVolumeClaimTableList extends V1ObjectTableList<PersistentVolumeClaimTable, V1PersistentVolumeClaim> {

    @Override
    protected PersistentVolumeClaimTable createInstance() {
        return new PersistentVolumeClaimTable();
    }

    @Override
    protected void putValueIntoField(final PersistentVolumeClaimTable builder, final String field, final String value) {

        switch (field) {
            case "name":
                builder.setName(value); break;
            case "status":
                builder.setStatus(value); break;
            case "volume":
                builder.setVolume(value); break;
            case "capacity":
                builder.setCapacity(value); break;
            case "access modes":
                builder.setAccessModes(value); break;
            case "storageclass":
                builder.setStorageClass(value); break;
            case "age":
                builder.setAge(value); break;
            case "volumemode":
                builder.setVolumeMode(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final PersistentVolumeClaimTable data, final V1ObjectAsTable<V1PersistentVolumeClaim> row) {
        // Do nothing
    }
}
