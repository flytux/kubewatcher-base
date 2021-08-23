package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.DeploymentTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Deployment;

public class AppsV1DeploymentTableList extends V1ObjectTableList<DeploymentTable, V1Deployment> {

    @Override
    protected DeploymentTable createInstance() {
        return new DeploymentTable();
    }

    @Override
    protected void putValueIntoField(final DeploymentTable builder, final String field, final String value) {

        switch (field) {
            case "name":
                builder.setName(value); break;
            case "ready":
                builder.setReady(value); break;
            case "up-to-date":
                builder.setUpToDate(value); break;
            case "available":
                builder.setAvailable(value); break;
            case "age":
                builder.setAge(value); break;
            case "containers":
                builder.setContainers(value); break;
            case "images":
                builder.setImages(value); break;
            case "selector":
                builder.setSelector(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final DeploymentTable data, final V1ObjectAsTable<V1Deployment> row) {
        // Do nothing
    }
}
