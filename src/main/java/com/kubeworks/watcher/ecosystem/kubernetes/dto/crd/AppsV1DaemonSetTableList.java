package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.DaemonSetTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1DaemonSet;

public class AppsV1DaemonSetTableList extends V1ObjectTableList<DaemonSetTable, V1DaemonSet> {

    @Override
    protected DaemonSetTable createInstance() {
        return new DaemonSetTable();
    }

    @Override
    protected void putValueIntoField(final DaemonSetTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "desired" :
                builder.setDesired(value); break;
            case "current" :
                builder.setCurrent(value); break;
            case "ready" :
                builder.setReady(value); break;
            case "up-to-date" :
                builder.setUpToDate(value); break;
            case "available" :
                builder.setAvailable(value); break;
            case "node selector" :
                builder.setNodeSelector(value); break;
            case "age" :
                builder.setAge(value); break;
            case "containers" :
                builder.setContainers(value); break;
            case "images" :
                builder.setImages(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final DaemonSetTable data, final V1ObjectAsTable<V1DaemonSet> row) {
        // Do nothing
    }
}
