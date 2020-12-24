package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1RoleBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class RbacV1RoleBindingTableList extends V1ObjectTableList<RoleBindingTable, V1RoleBinding> {
    @Override
    protected RoleBindingTable getDataObject() {
        return new RoleBindingTable();
    }

    @Override
    protected void makeObject(RoleBindingTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "role" :
                builder.setRole(value);
                break;
            case "age" :
                builder.setAge(value);
                break;
            case "users" :
                builder.setUsers(value);
                break;
            case "groups" :
                builder.setGroups(value);
                break;
            case "serviceaccounts" :
                builder.setServiceaccounts(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<RoleBindingTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<RoleBindingTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1RoleBinding> row : super.getRows()) {
            RoleBindingTable data = getDataObject();
            final List<String> cells = row.getCells();
            IntStream.range(0, cells.size()).forEach(index -> {
                String value = cells.get(index);
                V1ObjectColumnDefinition columnDefinition = super.getColumnDefinitions().get(index);
                String fieldName = columnDefinition.getName().toLowerCase();
                makeObject(data, fieldName, value);
            });
            if (row.getObject().getMetadata() != null) {
                data.setName(row.getObject().getMetadata().getName());
                data.setNamespace(row.getObject().getMetadata().getNamespace());
                if (row.getObject().getMetadata().getCreationTimestamp() != null) {
                    String age = ExternalConstants.getCurrentBetweenPeriod(row.getObject().getMetadata().getCreationTimestamp().toInstant().getMillis());
                    data.setAge(age);
                }
            }

            if (row.getObject().getRoleRef() != null) {
                String role = row.getObject().getRoleRef().getKind() + "/" + row.getObject().getRoleRef().getName();
                data.setRole(role);
            }

            list.add(data);
        }
        return list;
    }
}
