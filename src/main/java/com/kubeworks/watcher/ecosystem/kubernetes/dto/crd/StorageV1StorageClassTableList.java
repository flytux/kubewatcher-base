package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.StorageClassTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1CellTableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageV1StorageClassTableList extends V1CellTableList<StorageClassTable> {

    @Override
    protected StorageClassTable getDataObject() {
        return new StorageClassTable();
    }

    @Override
    protected void makeObject(StorageClassTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "provisioner" :
                builder.setProvisioner(value);
                break;
            case "reclaimpolicy" :
                builder.setReclaimPolicy(value);
                break;
            case "volumebindingmode" :
                builder.setVolumeBindingMode(value);
                break;
            case "allowvolumeexpansion" :
                builder.setAllowVolumeExpansion(value);
                break;
            case "age" :
                builder.setAge(value);
                break;
            default:
                break;
        }
    }
}
