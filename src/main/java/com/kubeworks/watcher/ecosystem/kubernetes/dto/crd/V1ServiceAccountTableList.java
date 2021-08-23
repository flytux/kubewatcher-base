package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceAccountTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1ServiceAccount;

public class V1ServiceAccountTableList extends V1ObjectTableList<ServiceAccountTable, V1ServiceAccount> {

    @Override
    protected ServiceAccountTable createInstance() {
        return new ServiceAccountTable();
    }

    @Override
    protected void putValueIntoField(final ServiceAccountTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "secrets" :
                builder.setSecrets(value); break;
            case "age" :
                builder.setAge(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final ServiceAccountTable data, final V1ObjectAsTable<V1ServiceAccount> row) {
        // Do nothing
    }
}
