package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ResourceQuotaTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1ResourceQuota;
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
public class V1ResourceQuotaTableList extends V1ObjectTableList<ResourceQuotaTable, V1ResourceQuota> {

    @Override
    protected ResourceQuotaTable getDataObject() {
        return new ResourceQuotaTable();
    }

    @Override
    protected void makeObject(ResourceQuotaTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name":
                builder.setName(value);
                break;
            case "age":
                builder.setAge(value);
                break;
            case "request":
                builder.setRequest(value);
                break;
            case "limit":
                builder.setLimit(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<ResourceQuotaTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<ResourceQuotaTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1ResourceQuota> row : super.getRows()) {
            ResourceQuotaTable data = getDataObject();
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
