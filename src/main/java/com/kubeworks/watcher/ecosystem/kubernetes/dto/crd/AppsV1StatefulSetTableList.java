package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.StatefulSetTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1StatefulSet;

public class AppsV1StatefulSetTableList extends V1ObjectTableList<StatefulSetTable, V1StatefulSet> {

    @Override
    protected StatefulSetTable createInstance() {
        return new StatefulSetTable();
    }

    @Override
    protected void putValueIntoField(final StatefulSetTable builder, final String field, final String value) {

        switch (field) {
            case "name":
                builder.setName(value); break;
            case "ready":
                builder.setReady(value); break;
            case "age":
                builder.setAge(value); break;
            case "containers":
                builder.setContainers(value); break;
            case "images":
                builder.setImages(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final StatefulSetTable data, final V1ObjectAsTable<V1StatefulSet> row) {
        // Do nothing
    }
}
