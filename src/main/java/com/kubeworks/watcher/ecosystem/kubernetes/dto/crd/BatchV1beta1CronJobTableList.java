package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.CronJobTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1beta1CronJob;

public class BatchV1beta1CronJobTableList extends V1ObjectTableList<CronJobTable, V1beta1CronJob> {

    @Override
    protected CronJobTable createInstance() {
        return new CronJobTable();
    }

    @Override
    protected void putValueIntoField(final CronJobTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "schedule" :
                builder.setSchedule(value); break;
            case "suspend" :
                builder.setSuspend(Boolean.parseBoolean(value)); break;
            case "active" :
                builder.setActive(Integer.parseInt(value)); break;
            case "last schedule" :
                builder.setLastSchedule(value); break;
            case "age" :
                builder.setAge(value); break;
            case "containers" :
                builder.setContainers(value); break;
            case "images" :
                builder.setImages(value); break;
            case "selector" :
                builder.setSelector(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final CronJobTable data, final V1ObjectAsTable<V1beta1CronJob> row) {
        // Do nothing
    }
}
