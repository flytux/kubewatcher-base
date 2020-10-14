package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PersistentVolumeClaimTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
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
public class V1PersistentVolumeClaimTableList extends V1ObjectTableList<PersistentVolumeClaimTable, V1PersistentVolumeClaim> {

    @Override
    protected PersistentVolumeClaimTable getDataObject() {
        return new PersistentVolumeClaimTable();
    }

    @Override
    public List<PersistentVolumeClaimTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<PersistentVolumeClaimTable> nodes = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1PersistentVolumeClaim> row : super.getRows()) {
            PersistentVolumeClaimTable data = getDataObject();
            final List<String> cells = row.getCells();
            IntStream.range(0, cells.size()).forEach(index -> {
                String value = cells.get(index);
                V1ObjectColumnDefinition columnDefinition = super.getColumnDefinitions().get(index);
                String fieldName = columnDefinition.getName().toLowerCase();
                makeObject(data, fieldName, value);
            });
            if (row.getObject().getMetadata() != null) {
                data.setNamespace(row.getObject().getMetadata().getNamespace());
            }
            nodes.add(data);
        }
        return nodes;
    }

    @Override
    protected void makeObject(PersistentVolumeClaimTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name":
                builder.setName(value);
                break;
            case "status":
                builder.setStatus(value);
                break;
            case "volume":
                builder.setVolume(value);
                break;
            case "capacity":
                builder.setCapacity(value);
                break;
            case "access modes":
                builder.setAccessModes(value);
                break;
            case "storageclass":
                builder.setStorageClass(value);
                break;
            case "age":
                builder.setAge(value);
                break;
            case "volumemode":
                builder.setVolumeMode(value);
                break;
            default:
                break;
        }
    }
}
