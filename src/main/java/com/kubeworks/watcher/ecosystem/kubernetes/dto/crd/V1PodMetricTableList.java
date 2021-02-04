package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1PodMetricTableList extends V1ObjectTableList<MetricTable, PodMetrics>  {


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

            Map<String, Quantity> quantityMap = rowObject.getContainers().stream()
                .map(ContainerMetrics::getUsage)
                .reduce((map1, map2) -> {
                    map2.forEach((key, value) -> map1.merge(key, value, ExternalConstants::addQuantity));
                    return map1;
                }).orElse(Collections.emptyMap());

            data.setCpu(quantityMap.get("cpu"));
            data.setMemory(quantityMap.get("memory"));

            list.add(data);
        }
        return list;
    }


}
