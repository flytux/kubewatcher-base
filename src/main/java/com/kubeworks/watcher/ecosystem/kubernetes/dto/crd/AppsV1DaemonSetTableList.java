package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.DaemonSetTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1DaemonSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class AppsV1DaemonSetTableList extends V1ObjectTableList<DaemonSetTable, V1DaemonSet> {

    @Override
    protected DaemonSetTable getDataObject() {
        return new DaemonSetTable();
    }

    @Override
    protected void makeObject(DaemonSetTable builder, String fieldName, String value) {

        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "desired" :
                builder.setDesired(value);
                break;
            case "current" :
                builder.setCurrent(value);
                break;
            case "ready" :
                builder.setReady(value);
                break;
            case "up-to-date" :
                builder.setUpToDate(value);
                break;
            case "available" :
                builder.setAvailable(value);
                break;
            case "node selector" :
                builder.setNodeSelector(value);
                break;
            case "age" :
                builder.setAge(value);
                break;
            case "containers" :
                builder.setContainers(value);
                break;
            case "images" :
                builder.setImages(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<DaemonSetTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<DaemonSetTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1DaemonSet> row : super.getRows()) {
            DaemonSetTable data = getDataObject();
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
