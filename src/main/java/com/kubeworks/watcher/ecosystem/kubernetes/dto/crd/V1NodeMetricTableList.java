package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter @Setter
public class V1NodeMetricTableList extends V1ObjectTableList<MetricTable, NodeMetrics> {

    @SerializedName("items")
    private List<NodeMetrics> items;

    @Override
    protected MetricTable createInstance() {
        return new MetricTable();
    }

    @Override
    protected void putValueIntoField(final MetricTable builder, final String field, final String value) {
        throw new UnsupportedOperationException("putValueIntoField not supported");
    }

    @Override
    protected void executeExtraProcess(final MetricTable data, final V1ObjectAsTable<NodeMetrics> row) {
        // Do nothing
    }

    @Override
    public List<MetricTable> createDataTableList() {

        if (Objects.isNull(getItems())) { return ImmutableList.of(); }

        List<MetricTable> list = new ArrayList<>(getItems().size());
        for (NodeMetrics rowObject : getItems()) {
            MetricTable data = createInstance();
            if (rowObject.getMetadata() != null) {
                data.setName(rowObject.getMetadata().getName());
            }

            if (rowObject.getUsage() != null) {
                data.setCpu(rowObject.getUsage().get("cpu"));
                data.setMemory(rowObject.getUsage().get("memory"));
            }
            list.add(data);
        }
        return list;
    }
}
