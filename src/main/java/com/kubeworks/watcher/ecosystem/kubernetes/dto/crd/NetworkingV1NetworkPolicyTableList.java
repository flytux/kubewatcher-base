package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NetworkPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1NetworkPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class NetworkingV1NetworkPolicyTableList extends V1ObjectTableList<NetworkPolicyTable, V1NetworkPolicy> {
    @Override
    protected NetworkPolicyTable getDataObject() {
        return new NetworkPolicyTable();
    }

    @Override
    protected void makeObject(NetworkPolicyTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "pod-selector" :
                builder.setPodSelector(value);
                break;
            case "age" :
                builder.setAge(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<NetworkPolicyTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<NetworkPolicyTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1NetworkPolicy> row : super.getRows()) {
            NetworkPolicyTable data = getDataObject();
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
