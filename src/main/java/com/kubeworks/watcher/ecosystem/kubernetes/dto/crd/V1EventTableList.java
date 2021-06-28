package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.EventTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Event;

public class V1EventTableList extends V1ObjectTableList<EventTable, V1Event> {

    @Override
    protected EventTable createInstance() {
        return new EventTable();
    }

    @Override
    protected void putValueIntoField(final EventTable builder, final String field, final String value) {

        switch (field) {
            case "last seen" :
                builder.setLastSeen(value); break;
            case "type" :
                builder.setType(value); break;
            case "reason" :
                builder.setReason(value); break;
            case "object" :
                builder.setObject(value); break;
            case "subobject" :
                builder.setSubObject(value); break;
            case "source" :
                builder.setSource(value); break;
            case "message" :
                builder.setMessage(value); break;
            case "first seen" :
                builder.setFirstSeen(value); break;
            case "count" :
                builder.setCount(value); break;
            case "name" :
                builder.setName(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final EventTable data, final V1ObjectAsTable<V1Event> row) {
        // Do nothing
    }
}
