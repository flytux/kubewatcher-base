package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.HPATable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1HorizontalPodAutoscaler;
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
public class AutoScalingV1HPATableList extends V1ObjectTableList<HPATable, V1HorizontalPodAutoscaler> {

    @Override
    protected HPATable getDataObject() {
        return new HPATable();
    }

    @Override
    protected void makeObject(HPATable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name":
                builder.setName(value);
                break;
            case "reference":
                builder.setReference(value);
                break;
            case "targets":
                builder.setTargets(value);
                break;
            case "minpods":
                builder.setMinPods(value);
                break;
            case "maxpods":
                builder.setMaxPods(value);
                break;
            case "replicas":
                builder.setReplicas(value);
                break;
            case "age":
                builder.setAge(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<HPATable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<HPATable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1HorizontalPodAutoscaler> row : super.getRows()) {
            HPATable data = getDataObject();
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
