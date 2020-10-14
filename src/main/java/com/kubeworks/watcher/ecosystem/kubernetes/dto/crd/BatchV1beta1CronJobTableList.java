package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.CronJobTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1beta1CronJob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class BatchV1beta1CronJobTableList extends V1ObjectTableList<CronJobTable, V1beta1CronJob> {

    @Override
    protected CronJobTable getDataObject() {
        return new CronJobTable();
    }

    @Override
    protected void makeObject(CronJobTable builder, String fieldName, String value) {

        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "schedule" :
                builder.setSchedule(value);
                break;
            case "suspend" :
                builder.setSuspend(Boolean.parseBoolean(value));
                break;
            case "active" :
                builder.setActive(Integer.parseInt(value));
                break;
            case "last schedule" :
                builder.setLastSchedule(value);
                break;
            case "age" :
                builder.setAge(value);
                break;
            case "containers" :
                builder.setContainers(value);
                break;
            case "images" :
                builder.setImages(value);
                break;
            case "selector" :
                builder.setSelector(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<CronJobTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<CronJobTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1beta1CronJob> row : super.getRows()) {
            CronJobTable data = getDataObject();
            final List<String> cells = row.getCells();
            IntStream.range(0, cells.size()).forEach(index -> {
                String value = cells.get(index);
                V1ObjectColumnDefinition columnDefinition = super.getColumnDefinitions().get(index);
                String fieldName = columnDefinition.getName().toLowerCase();
                makeObject(data, fieldName, value);
            });
            if (row.getObject().getMetadata() != null) {
                data.setNamespace(row.getObject().getMetadata().getNamespace());
            }
            list.add(data);
        }
        return list;
    }
}
