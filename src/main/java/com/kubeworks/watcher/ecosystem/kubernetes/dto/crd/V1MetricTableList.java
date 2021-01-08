package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1MetricTableList extends V1ObjectTableList<MetricTable, PodMetrics>  {


    @SerializedName("items")
    private List<PodMetrics> items;


    @Override
    protected void makeObject(MetricTable builder, String fieldName, String value) {
        // no implementation
    }

    @Override
    protected MetricTable getDataObject() {
        return new MetricTable();
    }

    @Override
    public List<MetricTable> getDataTable() {
        if (getItems() == null) {
            return Collections.emptyList();
        }

        List<MetricTable> list = new ArrayList<>(getItems().size());
        for (PodMetrics rowObject : getItems()) {
            MetricTable data = getDataObject();
            if (rowObject.getMetadata() != null) {
                data.setName(rowObject.getMetadata().getName());
                data.setNamespace(rowObject.getMetadata().getNamespace());
            }

            if (rowObject.getContainers() != null) {
                List<ContainerMetrics> metrics = rowObject.getContainers();

                for (ContainerMetrics metric : metrics) {
                    data.setCpu(metric.getUsage().get("cpu"));
                    data.setMemory(metric.getUsage().get("memory"));
                }
            }
            list.add(data);
        }
        return list;
    }
}
