package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.DeploymentTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Deployment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class AppsV1DeploymentTableList extends V1ObjectTableList<DeploymentTable, V1Deployment> {

    @Override
    protected DeploymentTable getDataObject() {
        return new DeploymentTable();
    }

    @Override
    protected void makeObject(DeploymentTable builder, String fieldName, String value) {

        switch (fieldName) {
            case "name":
                builder.setName(value);
                break;
            case "ready":
                builder.setReady(value);
                break;
            case "up-to-date":
                builder.setUpToDate(value);
                break;
            case "available":
                builder.setAvailable(value);
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
            case "selector":
                builder.setSelector(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<DeploymentTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<DeploymentTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1Deployment> row : super.getRows()) {
            DeploymentTable data = getDataObject();
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
