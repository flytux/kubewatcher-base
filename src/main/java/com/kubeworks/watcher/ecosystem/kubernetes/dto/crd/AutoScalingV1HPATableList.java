package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.HPATable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1HorizontalPodAutoscaler;

public class AutoScalingV1HPATableList extends V1ObjectTableList<HPATable, V1HorizontalPodAutoscaler> {

    @Override
    protected HPATable createInstance() {
        return new HPATable();
    }

    @Override
    protected void putValueIntoField(final HPATable builder, final String field, final String value) {

        switch (field) {
            case "name":
                builder.setName(value); break;
            case "reference":
                builder.setReference(value); break;
            case "targets":
                builder.setTargets(value); break;
            case "minpods":
                builder.setMinPods(value); break;
            case "maxpods":
                builder.setMaxPods(value); break;
            case "replicas":
                builder.setReplicas(value); break;
            case "age":
                builder.setAge(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final HPATable data, final V1ObjectAsTable<V1HorizontalPodAutoscaler> row) {
        // Do nothing
    }
}
