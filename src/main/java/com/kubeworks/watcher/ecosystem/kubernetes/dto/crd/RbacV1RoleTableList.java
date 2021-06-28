package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Role;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.Objects;

@Slf4j
public class RbacV1RoleTableList extends V1ObjectTableList<RoleTable, V1Role> {

    @Override
    protected RoleTable createInstance() {
        return new RoleTable();
    }

    @Override
    protected void putValueIntoField(final RoleTable builder, final String field, final String value) {
        if ("name".equals(field)) { builder.setName(value); }
    }

    @Override
    protected void executeExtraProcess(final RoleTable data, final V1ObjectAsTable<V1Role> row) {

        if (Objects.nonNull(row.getObject().getMetadata())) {
            final DateTime dateTime = row.getObject().getMetadata().getCreationTimestamp();
            if (Objects.nonNull(dateTime)) {
                data.setAge(ExternalConstants.getBetweenPeriodDay(dateTime.toInstant().getMillis()));
            }
        }
    }
}
