package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EndpointTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodSecurityPolicyTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.RoleTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Endpoints;
import io.kubernetes.client.openapi.models.V1ManagedFieldsEntry;
import io.kubernetes.client.openapi.models.PolicyV1beta1PodSecurityPolicy;
import io.kubernetes.client.openapi.models.V1Role;
import org.apache.commons.lang3.StringUtils;

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
            case "namespace" :
                builder.setNamespace(value);
                break;
            case "age" :
                builder.setAge(value);
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
            }
            if (row.getObject().getMetadata().getManagedFields() != null) {
                List<V1ManagedFieldsEntry> managedFields = row.getObject().getMetadata().getManagedFields();
                String statusStr = managedFields.stream()
                    .filter(field -> StringUtils.equalsIgnoreCase("kube-controller-manager", field.getManager())
                        && field.getFieldsV1() != null)
                    .map(field -> {
                        ObjectNode jsonNode = ExternalConstants.OBJECT_MAPPER.convertValue(field.getFieldsV1(), ObjectNode.class);
                        for (Iterator<String> fieldNames = jsonNode.at("/f:status/f:conditions").fieldNames(); fieldNames.hasNext(); ) {
                            String fieldName = fieldNames.next();
                            if (!StringUtils.equalsIgnoreCase(".", fieldName)) {
                                fieldName = StringUtils.substring(fieldName, 11, fieldName.lastIndexOf("\"}"));
                                return fieldName;
                            }
                        }
                        return null;
                    }).filter(Objects::nonNull).findFirst().orElse(ExternalConstants.NONE);
            }
            list.add(data);
        }
        return list;
    }
}
