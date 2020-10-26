package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NamespaceTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1CellTableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1NamespaceTableList extends V1CellTableList<NamespaceTable> {

    @Override
    protected NamespaceTable getDataObject() {
        return new NamespaceTable();
    }

    @Override
    protected void makeObject(NamespaceTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name":
                builder.setName(value);
                break;
            case "status":
                builder.setStatus(value);
                break;
            case "age":
                builder.setAge(value);
                break;
            default:
                break;
        }
    }

}
