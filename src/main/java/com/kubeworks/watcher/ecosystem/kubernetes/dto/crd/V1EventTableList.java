package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.EventTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Event;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1EventTableList extends V1ObjectTableList<EventTable, V1Event> {

    @Override
    protected EventTable getDataObject() {
        return new EventTable();
    }

    @Override
    protected void makeObject(EventTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "last seen" :
                builder.setLastSeen(value);
                break;
            case "type" :
                builder.setType(value);
                break;
            case "reason" :
                builder.setReason(value);
                break;
            case "object" :
                builder.setObject(value);
                break;
            case "subobject" :
                builder.setSubObject(value);
                break;
            case "source" :
                builder.setSource(value);
                break;
            case "message" :
                builder.setMessage(value);
                break;
            case "first seen" :
                builder.setFirstSeen(value);
                break;
            case "count" :
                builder.setCount(value);
                break;
            case "name" :
                builder.setName(value);
                break;
            default:
                break;
        }
    }

    @Override
    public List<EventTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<EventTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1Event> row : super.getRows()) {
            EventTable data = getDataObject();
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
