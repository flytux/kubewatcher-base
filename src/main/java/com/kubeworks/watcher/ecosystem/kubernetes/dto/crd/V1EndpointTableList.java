package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Endpoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class V1EndpointTableList extends V1ObjectTableList<EndpointTable, V1Endpoints> {

    @Override
    protected EndpointTable getDataObject() {
        return new EndpointTable();
    }

    @Override
    protected void makeObject(EndpointTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "endpoints" :
                builder.setEndpoints(value);
                break;
            case "age" :
                builder.setAge(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<EndpointTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<EndpointTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1Endpoints> row : super.getRows()) {
            EndpointTable data = getDataObject();
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
