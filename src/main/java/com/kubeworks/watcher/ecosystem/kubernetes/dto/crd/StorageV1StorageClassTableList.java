package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.StorageClassTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1StorageClass;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class StorageV1StorageClassTableList extends V1ObjectTableList<StorageClassTable, V1StorageClass> {

    @Override
    protected StorageClassTable createInstance() {
        return new StorageClassTable();
    }

    @Override
    protected void putValueIntoField(final StorageClassTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "provisioner" :
                builder.setProvisioner(value); break;
            case "reclaimpolicy" :
                builder.setReclaimPolicy(value); break;
            case "volumebindingmode" :
                builder.setVolumeBindingMode(value); break;
            case "allowvolumeexpansion" :
                builder.setAllowVolumeExpansion(value); break;
            case "age" :
                builder.setAge(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final StorageClassTable data, final V1ObjectAsTable<V1StorageClass> row) {
        if (Objects.nonNull(row.getObject().getMetadata())) { data.setName(row.getObject().getMetadata().getName()); }
    }
}
