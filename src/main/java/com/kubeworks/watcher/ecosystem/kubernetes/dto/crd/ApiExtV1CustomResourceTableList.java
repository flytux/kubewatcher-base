package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CustomResourceTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinition;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinitionVersion;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiExtV1CustomResourceTableList extends V1ObjectTableList<CustomResourceTable, V1CustomResourceDefinition> {

    @SerializedName("items")
    private List<V1CustomResourceDefinition> items;

    @Override
    protected CustomResourceTable getDataObject() {
        return new CustomResourceTable();
    }

    @Override
    protected void makeObject(CustomResourceTable builder, String fieldName, String value) {
        // no implementation
    }

    @Override
    public List<CustomResourceTable> getDataTable() {
        if (getItems() == null) {
            return Collections.emptyList();
        }

        List<CustomResourceTable> list = new ArrayList<>(getItems().size());
        for (V1CustomResourceDefinition rowObject : getItems()) {
            CustomResourceTable data = getDataObject();
            if (rowObject.getMetadata() != null) {
                data.setName(rowObject.getMetadata().getName());
                if (rowObject.getMetadata().getCreationTimestamp() != null) {
                    data.setAge(ExternalConstants.getCurrentBetweenPeriod(rowObject.getMetadata().getCreationTimestamp().toInstant().getMillis()));
                }
            }
            if (rowObject.getSpec() != null) {
                data.setGroup(rowObject.getSpec().getGroup());
                data.setScope(rowObject.getSpec().getScope());
                String versions = rowObject.getSpec().getVersions().stream()
                    .filter(Objects::nonNull)
                    .map(V1CustomResourceDefinitionVersion::getName)
                    .collect(Collectors.joining(", "));
                data.setVersion(versions);
            }

            list.add(data);
        }
        return list;
    }

}
