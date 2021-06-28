package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Endpoints;

public class V1EndpointTableList extends V1ObjectTableList<EndpointTable, V1Endpoints> {

    @Override
    protected EndpointTable createInstance() {
        return new EndpointTable();
    }

    @Override
    protected void putValueIntoField(final EndpointTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "endpoints" :
                builder.setEndpoints(value); break;
            case "age" :
                builder.setAge(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final EndpointTable data, final V1ObjectAsTable<V1Endpoints> row) {
        // Do nothing
    }
}
