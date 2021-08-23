package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ComponentStatusTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1CellTableList;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class V1ComponentStatusTableList extends V1CellTableList<ComponentStatusTable> {

    @Override
    protected ComponentStatusTable getDataObject() {
        return new ComponentStatusTable();
    }

    @Override
    protected void makeObject(final ComponentStatusTable builder, final String field, final String value) {

        switch (field) {
            case "name":
                builder.setName(value); break;
            case "status":
                builder.setStatus(value); break;
            case "message":
                builder.setMessage(value); break;
            case "error":
                builder.setError(value); break;
            default: break;
        }
    }
}
