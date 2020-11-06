package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.IngressTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.NetworkingV1beta1Ingress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class NetworkingV1beta1IngressTableList extends V1ObjectTableList<IngressTable, NetworkingV1beta1Ingress> {

    @Override
    protected IngressTable getDataObject() {
        return new IngressTable();
    }

    @Override
    protected void makeObject(IngressTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "class" :
                builder.setIngressClass(value);
                break;
            case "hosts" :
                builder.setHosts(value);
                break;
            case "address" :
                builder.setAddress(value);
                break;
            case "ports" :
                builder.setPorts(value);
                break;
            case "age" :
                builder.setAge(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<IngressTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<IngressTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<NetworkingV1beta1Ingress> row : super.getRows()) {
            IngressTable data = getDataObject();
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
