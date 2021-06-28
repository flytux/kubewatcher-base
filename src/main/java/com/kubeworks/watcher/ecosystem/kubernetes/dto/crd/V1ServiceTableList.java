package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ServiceTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Service;

public class V1ServiceTableList extends V1ObjectTableList<ServiceTable, V1Service> {

    @Override
    protected ServiceTable createInstance() {
        return new ServiceTable();
    }

    @Override
    protected void putValueIntoField(final ServiceTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "type" :
                builder.setType(value); break;
            case "cluster-ip" :
                builder.setClusterIp(value); break;
            case "external-ip" :
                builder.setExternalIp(value); break;
            case "port(s)" :
                builder.setPorts(value); break;
            case "age" :
                builder.setAge(value); break;
            case "selector" :
                builder.setSelector(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final ServiceTable data, final V1ObjectAsTable<V1Service> row) {
        // Do nothing
    }
}
