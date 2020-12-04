package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.StorageClassTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1StorageClass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageV1StorageClassTableList extends V1ObjectTableList<StorageClassTable, V1StorageClass> {

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

    @Override
    public List<StorageClassTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<StorageClassTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1StorageClass> row : super.getRows()) {
            StorageClassTable data = getDataObject();
            final List<String> cells = row.getCells();
            IntStream.range(0, cells.size()).forEach(index -> {
                String value = cells.get(index);
                V1ObjectColumnDefinition columnDefinition = super.getColumnDefinitions().get(index);
                String fieldName = columnDefinition.getName().toLowerCase();
                makeObject(data, fieldName, value);
            });
            if (row.getObject().getMetadata() != null) {
                data.setName(row.getObject().getMetadata().getName());
            }
            list.add(data);
        }
        return list;
    }

}
