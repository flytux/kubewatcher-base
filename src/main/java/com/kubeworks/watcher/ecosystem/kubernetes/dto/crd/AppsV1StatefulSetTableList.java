package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.StatefulSetTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1StatefulSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class AppsV1StatefulSetTableList extends V1ObjectTableList<StatefulSetTable, V1StatefulSet> {

    @Override
    protected StatefulSetTable getDataObject() {
        return new StatefulSetTable();
    }

    @Override
    protected void makeObject(StatefulSetTable builder, String fieldName, String value) {

        switch (fieldName) {
            case "name":
                builder.setName(value);
                break;
            case "ready":
                builder.setReady(value);
                break;
            case "age":
                builder.setAge(value);
                break;
            case "containers":
                builder.setContainers(value);
                break;
            case "images":
                builder.setImages(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<StatefulSetTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<StatefulSetTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1StatefulSet> row : super.getRows()) {
            StatefulSetTable data = getDataObject();
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
            list.add(data);
        }
        return list;
    }
}
