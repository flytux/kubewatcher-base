package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.LimitRangeTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1LimitRange;

public class V1LimitRangeTableList extends V1ObjectTableList<LimitRangeTable, V1LimitRange> {

    @Override
    protected LimitRangeTable createInstance() {
        return new LimitRangeTable();
    }

    @Override
    protected void putValueIntoField(final LimitRangeTable builder, final String field, final String value) {

        switch (field) {
            case "name":
                builder.setName(value); break;
            case "created at":
                builder.setCreatedAt(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final LimitRangeTable data, final V1ObjectAsTable<V1LimitRange> row) {
        // Do nothing
    }
}
