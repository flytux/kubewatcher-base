package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.NodeTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1CellTableList;

public class V1NodeTableList extends V1CellTableList<NodeTable> {

    @Override
    protected NodeTable getDataObject() {
        return new NodeTable();
    }

    @Override
    protected void makeObject(final NodeTable builder, final String field, final String value) {

        switch (field) {
            case "name":
                builder.setName(value); break;
            case "status":
                builder.setStatus(value); break;
            case "roles":
                builder.setRoles(value); break;
            case "age":
                builder.setAge(value); break;
            case "version":
                builder.setVersion(value); break;
            case "internal-ip":
                builder.setInternalIp(value); break;
            case "external-ip":
                builder.setExternalIp(value); break;
            case "os-image":
                builder.setOsImage(value); break;
            case "kernel-version":
                builder.setKernelVersion(value); break;
            case "container-runtime":
                builder.setContainerRuntime(value); break;
            default: break;
        }
    }
}
