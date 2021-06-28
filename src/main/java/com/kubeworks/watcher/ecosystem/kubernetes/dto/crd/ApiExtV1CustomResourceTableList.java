package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CustomResourceTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinition;
import io.kubernetes.client.openapi.models.V1CustomResourceDefinitionVersion;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter @Setter
public class ApiExtV1CustomResourceTableList extends V1ObjectTableList<CustomResourceTable, V1CustomResourceDefinition> {

    @SerializedName("items")
    private List<V1CustomResourceDefinition> items;

    @Override
    protected CustomResourceTable createInstance() {
        return new CustomResourceTable();
    }

    @Override
    protected void putValueIntoField(final CustomResourceTable builder, final String field, final String value) {
        throw new UnsupportedOperationException("putValueIntoField not supported");
    }

    @Override
    protected void executeExtraProcess(final CustomResourceTable data, final V1ObjectAsTable<V1CustomResourceDefinition> row) {
        // Do nothing
    }

    @Override
    public List<CustomResourceTable> createDataTableList() {

        if (Objects.isNull(getItems())) { return ImmutableList.of(); }

        List<CustomResourceTable> list = new ArrayList<>(getItems().size());
        for (V1CustomResourceDefinition rowObject : getItems()) {
            CustomResourceTable data = createInstance();
            if (rowObject.getMetadata() != null) {
                data.setName(rowObject.getMetadata().getName());
                if (rowObject.getMetadata().getCreationTimestamp() != null) {
                    data.setAge(ExternalConstants.getBetweenPeriodDay(rowObject.getMetadata().getCreationTimestamp().toInstant().getMillis()));
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
