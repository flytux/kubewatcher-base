package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleBindingTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1RoleBinding;
import io.kubernetes.client.openapi.models.V1RoleRef;

import java.util.Objects;

public class RbacV1RoleBindingTableList extends V1ObjectTableList<RoleBindingTable, V1RoleBinding> {
    @Override
    protected RoleBindingTable createInstance() {
        return new RoleBindingTable();
    }

    @Override
    protected void putValueIntoField(final RoleBindingTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "role" :
                builder.setRole(value); break;
            case "age" :
                builder.setAge(value); break;
            case "users" :
                builder.setUsers(value); break;
            case "groups" :
                builder.setGroups(value); break;
            case "serviceaccounts" :
                builder.setServiceaccounts(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final RoleBindingTable data, final V1ObjectAsTable<V1RoleBinding> row) {

        final V1RoleBinding binding = row.getObject();
        final V1ObjectMeta meta = binding.getMetadata();

        if (Objects.nonNull(meta)) {
            data.setName(meta.getName());

            if (Objects.nonNull(meta.getCreationTimestamp())) {
                data.setAge(ExternalConstants.getBetweenPeriodDay(meta.getCreationTimestamp().toInstant().getMillis()));
            }
        }

        final V1RoleRef ref = binding.getRoleRef();

        if (Objects.nonNull(ref)) { data.setRole(ref.getKind() + "/" + ref.getName()); }
    }
}
