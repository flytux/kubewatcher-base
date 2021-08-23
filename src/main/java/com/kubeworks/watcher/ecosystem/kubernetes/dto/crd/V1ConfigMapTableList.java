package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ConfigMapTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1ConfigMap;

public class V1ConfigMapTableList extends V1ObjectTableList<ConfigMapTable, V1ConfigMap> {

    @Override
    protected ConfigMapTable createInstance() {
        return new ConfigMapTable();
    }

    @Override
    protected void putValueIntoField(final ConfigMapTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "data" :
                builder.setDataCount(value); break;
            case "age" :
                builder.setAge(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final ConfigMapTable data, final V1ObjectAsTable<V1ConfigMap> row) {
        // Do nothing
    }
}
