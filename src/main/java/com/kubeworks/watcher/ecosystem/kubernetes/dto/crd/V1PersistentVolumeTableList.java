package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PersistentVolumeTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1CellTableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1PersistentVolumeTableList extends V1CellTableList<PersistentVolumeTable> {

    @Override
    protected PersistentVolumeTable getDataObject() {
        return new PersistentVolumeTable();
    }

    @Override
    protected void makeObject(PersistentVolumeTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "capacity" :
                builder.setCapacity(value);
                break;
            case "access modes" :
                builder.setAccessModes(value);
                break;
            case "reclaim policy" :
                builder.setReclaimPolicy(value);
                break;
            case "status" :
                builder.setStatus(value);
                break;
            case "claim" :
                builder.setClaim(value);
                break;
            case "storageclass" :
                builder.setStorageClass(value);
                break;
            case "reason" :
                builder.setReason(value);
                break;
            case "age" :
                builder.setAge(value);
                break;
            case "volumemode" :
                builder.setVolumeMode(value);
                break;

            default:
                break;
        }
    }
}
