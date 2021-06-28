package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ResourceQuotaTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1ResourceQuota;

public class V1ResourceQuotaTableList extends V1ObjectTableList<ResourceQuotaTable, V1ResourceQuota> {

    @Override
    protected ResourceQuotaTable createInstance() {
        return new ResourceQuotaTable();
    }

    @Override
    protected void putValueIntoField(final ResourceQuotaTable builder, final String field, final String value) {

        switch (field) {
            case "name":
                builder.setName(value); break;
            case "age":
                builder.setAge(value); break;
            case "request":
                builder.setRequest(value); break;
            case "limit":
                builder.setLimit(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final ResourceQuotaTable data, final V1ObjectAsTable<V1ResourceQuota> row) {
        // Do nothing
    }
}
