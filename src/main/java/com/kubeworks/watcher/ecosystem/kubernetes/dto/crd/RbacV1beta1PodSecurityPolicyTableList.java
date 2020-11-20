package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodSecurityPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.PolicyV1beta1PodSecurityPolicy;

import java.util.*;
import java.util.stream.IntStream;

public class RbacV1beta1PodSecurityPolicyTableList extends V1ObjectTableList<PodSecurityPolicyTable, PolicyV1beta1PodSecurityPolicy> {

    @Override
    protected PodSecurityPolicyTable getDataObject() {
        return new PodSecurityPolicyTable();
    }

    @Override
    protected void makeObject(PodSecurityPolicyTable builder, String fieldName, String value) {
        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "priv" :
                builder.setPrivileged(value);
                break;
            case "volumes" :
                builder.setVolumes(value);
                break;
        }
    }

    @Override
    public List<PodSecurityPolicyTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<PodSecurityPolicyTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<PolicyV1beta1PodSecurityPolicy> row : super.getRows()) {
            PodSecurityPolicyTable data = getDataObject();
            final List<String> cells = row.getCells();
            IntStream.range(0, cells.size()).forEach(index -> {
                String value = cells.get(index);
                V1ObjectColumnDefinition columnDefinition = super.getColumnDefinitions().get(index);
                String fieldName = columnDefinition.getName().toLowerCase();

                makeObject(data, fieldName, value);
            });
            if (row.getObject().getMetadata() != null) {
                data.setNamespace(row.getObject().getMetadata().getNamespace());
                data.setAge(ExternalConstants.getBetweenPeriodDay(row.getObject().getMetadata().getCreationTimestamp().toInstant().getMillis()));
            }
            list.add(data);
        }
        return list;
    }
}
