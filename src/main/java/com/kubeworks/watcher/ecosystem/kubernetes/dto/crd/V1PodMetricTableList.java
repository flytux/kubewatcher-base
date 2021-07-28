package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.common.collect.ImmutableList;
import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.MetricTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectAsTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.custom.Quantity;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter @Setter
public class V1PodMetricTableList extends V1ObjectTableList<MetricTable, PodMetrics>  {

    @SerializedName("items")
    private List<PodMetrics> items;

    @Override
    protected void putValueIntoField(final MetricTable builder, final String field, final String value) {
        throw new UnsupportedOperationException("putValueIntoField not supported");
    }

    @Override
    protected MetricTable createInstance() {
        return new MetricTable();
    }

    @Override
    protected void executeExtraProcess(final MetricTable data, final V1ObjectAsTable<PodMetrics> row) {
        // Do nothing
    }

    @Override
    public List<MetricTable> createDataTableList() {

        if (Objects.isNull(getItems())) { return ImmutableList.of(); }

        List<MetricTable> list = new ArrayList<>(getItems().size());
        for (PodMetrics rowObject : getItems()) {
            MetricTable data = createInstance();
            if (rowObject.getMetadata() != null) {
                data.setName(rowObject.getMetadata().getName());
                data.setNamespace(rowObject.getMetadata().getNamespace());
            }

            Map<String, Quantity> quantityMap = rowObject.getContainers().stream()
                .map(ContainerMetrics::getUsage)
                .reduce((map1, map2) -> {
                    map2.forEach((key, value) -> map1.merge(key, value, ExternalConstants::addQuantity));
                    return map1;
                }).orElseGet(Collections::emptyMap);

            data.setCpu(quantityMap.get("cpu"));
            data.setMemory(quantityMap.get("memory"));

            list.add(data);
        }

        return list;
    }
}
