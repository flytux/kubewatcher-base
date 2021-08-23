package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.ObjectMapperHolder;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.JobTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1ManagedFieldsEntry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BatchV1JobTableList extends V1ObjectTableList<JobTable, V1Job> {

    @Override
    protected JobTable createInstance() {
        return new JobTable();
    }

    @Override
    protected void putValueIntoField(final JobTable builder, final String field, final String value) {

        switch (field) {
            case "name" :
                builder.setName(value); break;
            case "completions" :
                builder.setCompletions(value); break;
            case "duration" :
                builder.setDuration(value); break;
            case "age" :
                builder.setAge(value); break;
            case "containers" :
                builder.setContainers(value); break;
            case "images" :
                builder.setImages(value); break;
            case "selector" :
                builder.setSelector(value); break;
            default: break;
        }
    }

    @Override
    protected void executeExtraProcess(final JobTable data, final V1ObjectAsTable<V1Job> row) {

        if (Objects.nonNull(row.getObject().getMetadata())) {
            final List<V1ManagedFieldsEntry> fields = row.getObject().getMetadata().getManagedFields();

            if (!CollectionUtils.isEmpty(fields)) {
                data.setStatusCondition(extractStatusFrom(fields));
            }
        }
    }

    private String extractStatusFrom(final List<V1ManagedFieldsEntry> source) {

        return source.stream()
            .filter(e -> StringUtils.equalsIgnoreCase("kube-controller-manager", e.getManager()) && Objects.nonNull(e.getFieldsV1()))
            .map(e -> {
                final ObjectNode node = ObjectMapperHolder.retrieveObjectMapper().convertValue(e.getFieldsV1(), ObjectNode.class);
                for (Iterator<String> names = node.at("/f:status/f:conditions").fieldNames(); names.hasNext();) {
                    final String name = names.next();
                    if (!StringUtils.equalsIgnoreCase(".", name)) {
                        return StringUtils.substring(name, 11, name.lastIndexOf("\"}"));
                    }
                }

                return null;
            }).filter(Objects::nonNull).findFirst().orElse(ExternalConstants.NONE);
    }
}
