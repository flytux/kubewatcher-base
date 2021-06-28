package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Pod;

public class V1PodTableList extends V1ObjectTableList<PodTable, V1Pod> {

    @Override
    protected PodTable createInstance() {
        return new PodTable();
    }

    @Override
    protected void putValueIntoField(final PodTable builder, final String field, final String value) {

        switch (field) {
            case "name":
                builder.setName(value); break;
            case "ready":
                builder.setReady(value); break;
            case "status":
                builder.setStatus(value); break;
            case "restarts":
                builder.setRestarts(value); break;
            case "age":
                builder.setAge(value); break;
            case "ip":
                builder.setIp(value); break;
            case "node":
                builder.setNode(value); break;
            case "nominated node":
                builder.setNominatedNode(value); break;
            case "readiness gates":
                builder.setReadinessGates(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final PodTable data, final V1ObjectAsTable<V1Pod> row) {
        // Do nothing
    }
}
