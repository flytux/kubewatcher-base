package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.PodTable;
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
public class V1PodMetricTableList extends V1ObjectTableList<PodTable, PodMetrics>  {


    @SerializedName("items")
    private List<PodMetrics> items;


    @Override
    protected void makeObject(PodTable builder, String fieldName, String value) {
        // no implementation
    }

    @Override
    protected PodTable getDataObject() {
        return new PodTable();
    }

    @Override
    public List<PodTable> getDataTable() {
        if (getItems() == null) {
            return Collections.emptyList();
        }

        List<PodTable> list = new ArrayList<>(getItems().size());
        for (PodMetrics rowObject : getItems()) {
            PodTable data = getDataObject();
            if (rowObject.getMetadata() != null) {
                data.setName(rowObject.getMetadata().getName());
                data.setNamespace(rowObject.getMetadata().getNamespace());
            }

            if (rowObject.getContainers() != null) {
                List<ContainerMetrics> metric = rowObject.getContainers();
                for (int i=0; i<metric.size(); i++) {
                    data.setCpu(metric.get(i).getUsage().get("cpu"));
                    data.setMemory(metric.get(i).getUsage().get("memory"));
                }
            }
            list.add(data);
        }
        return list;
    }
}
