package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.EventTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1CellTableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1EventTableList extends V1CellTableList<EventTable> {

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

}
