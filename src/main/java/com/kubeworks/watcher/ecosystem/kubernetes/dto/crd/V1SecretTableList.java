package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.SecretTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Secret;

public class V1SecretTableList extends V1ObjectTableList<SecretTable, V1Secret> {

    @Override
    protected SecretTable createInstance() {
        return new SecretTable();
    }

    @Override
    protected void putValueIntoField(final SecretTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "type" :
                builder.setType(value); break;
            case "data" :
                builder.setDataCount(value); break;
            case "age" :
                builder.setAge(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final SecretTable data, final V1ObjectAsTable<V1Secret> row) {
        // Do nothing
    }
}
