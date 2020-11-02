package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.JobTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1ManagedFieldsEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.IntStream;

public class BatchV1JobTableList extends V1ObjectTableList<JobTable, V1Job> {

    @Override
    protected JobTable getDataObject() {
        return new JobTable();
    }

    @Override
    protected void makeObject(JobTable builder, String fieldName, String value) {

        switch (fieldName) {
            case "name" :
                builder.setName(value);
                break;
            case "completions" :
                builder.setCompletions(value);
                break;
            case "duration" :
                builder.setDuration(value);
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
    public List<JobTable> getDataTable() {
        if (super.getRows() == null || super.getColumnDefinitions() == null) {
            return Collections.emptyList();
        }

        List<JobTable> list = new ArrayList<>(super.getRows().size());
        for (V1ObjectAsTable<V1Job> row : super.getRows()) {
            JobTable data = getDataObject();
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
                data.setStatusCondition(statusStr);
            }

            list.add(data);
        }
        return list;
    }
}
