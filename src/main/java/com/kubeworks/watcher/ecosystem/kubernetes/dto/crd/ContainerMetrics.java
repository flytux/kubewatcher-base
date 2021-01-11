package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ContainerMetrics {
    private String name;
    private Map<String, Quantity> usage;

    public ContainerMetrics() {
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Quantity> getUsage() {
        return this.usage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsage(Map<String, Quantity> usage) {
        this.usage = usage;
    }
}
