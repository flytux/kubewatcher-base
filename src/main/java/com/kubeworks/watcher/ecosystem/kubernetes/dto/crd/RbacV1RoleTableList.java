package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Role;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class RbacV1RoleTableList extends V1ObjectTableList<RoleTable, V1Role> {
    @Override
    protected RoleTable getDataObject() {
        return new RoleTable();
    }

    @Override
    protected void makeObject(RoleTable builder, String fieldName, String value) {
    }

    @Override
    public List<RoleTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<RoleTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1Role> row : super.getRows()) {
            RoleTable data = getDataObject();
            final List<String> cells = row.getCells();
            IntStream.range(0, cells.size()).forEach(index -> {
                String value = cells.get(index);
                V1ObjectColumnDefinition columnDefinition = super.getColumnDefinitions().get(index);
                String fieldName = columnDefinition.getName().toLowerCase();

                if (fieldName.equals("name")) data.setName(value);
            });
            if (row.getObject().getMetadata() != null) {
                data.setNamespace(row.getObject().getMetadata().getNamespace());
                data.setAge(ExternalConstants.getBetweenPeriodDay(row.getObject().getMetadata().getCreationTimestamp().toInstant().getMillis()));
            }
            list.add(data);
        }
        return list;
    }
}
