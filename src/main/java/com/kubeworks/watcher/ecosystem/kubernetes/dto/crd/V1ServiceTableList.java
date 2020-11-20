package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import io.kubernetes.client.openapi.models.V1Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

public class V1ServiceTableList extends V1ObjectTableList<ServiceTable, V1Service> {

    @Override
    protected ServiceTable getDataObject() {
        return new ServiceTable();
    }

    @Override
    protected void makeObject(ServiceTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "type" :
                builder.setType(value);
                break;
            case "cluster-ip" :
                builder.setClusterIp(value);
                break;
            case "external-ip" :
                builder.setExternalIp(value);
                break;
            case "port(s)" :
                builder.setPorts(value);
                break;
            case "age" :
                builder.setAge(value);
                break;
            case "selector" :
                builder.setSelector(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<ServiceTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<ServiceTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1Service> row : super.getRows()) {
            ServiceTable data = getDataObject();
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
