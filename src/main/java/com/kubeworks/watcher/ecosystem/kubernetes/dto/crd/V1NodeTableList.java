package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NodeTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1CellTableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1NodeTableList extends V1CellTableList<NodeTable> {

    @Override
    protected NodeTable getDataObject() {
        return new NodeTable();
    }

    @Override
    protected void makeObject(NodeTable nodeTable, String fieldName, String value) {
        switch (fieldName) {
            case "name":
                nodeTable.setName(value);
                break;
            case "status":
                nodeTable.setStatus(value);
                break;
            case "roles":
                nodeTable.setRoles(value);
                break;
            case "age":
                nodeTable.setAge(value);
                break;
            case "version":
                nodeTable.setVersion(value);
                break;
            case "internal-ip":
                nodeTable.setInternalIp(value);
                break;
            case "external-ip":
                nodeTable.setExternalIp(value);
                break;
            case "os-image":
                nodeTable.setOsImage(value);
                break;
            case "kernel-version":
                nodeTable.setKernelVersion(value);
                break;
            case "container-runtime":
                nodeTable.setContainerRuntime(value);
                break;
            default:
                break;
        }
    }



}
