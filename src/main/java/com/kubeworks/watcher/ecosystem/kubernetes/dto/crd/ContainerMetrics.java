package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.common.collect.ImmutableMap;
import io.kubernetes.client.custom.Quantity;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

@Getter @Setter
public class ContainerMetrics {

    private String name;
    private Map<String, Quantity> usage;

    public void setUsage(final Map<String, Quantity> usage) {
        this.usage = Objects.nonNull(usage) ? usage : ImmutableMap.of();
    }
}
