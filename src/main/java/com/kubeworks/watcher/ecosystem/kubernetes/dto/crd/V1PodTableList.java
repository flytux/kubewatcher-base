package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1Pod;
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
public class V1PodTableList extends V1ObjectTableList<PodTable, V1Pod> {

    @Override
    protected PodTable getDataObject() {
        return new PodTable();
    }

    @Override
    public List<PodTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<PodTable> nodes = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1Pod> row : super.getRows()) {
            PodTable data = getDataObject();
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
    protected void makeObject(PodTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name":
                builder.setName(value);
                break;
            case "ready":
                builder.setReady(value);
                break;
            case "status":
                builder.setStatus(value);
                break;
            case "restarts":
                builder.setRestarts(value);
                break;
            case "age":
                builder.setAge(value);
                break;
            case "ip":
                builder.setIp(value);
                break;
            case "node":
                builder.setNode(value);
                break;
            case "nominated node":
                builder.setNominatedNode(value);
                break;
            case "readiness gates":
                builder.setReadinessGates(value);
                break;
            case "cpu":
                builder.setCpu(Quantity.fromString(value));
                break;
            case "memory":
                builder.setMemory(Quantity.fromString(value));
                break;
            default:
                break;
        }
    }
}
