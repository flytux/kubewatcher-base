package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContainerMetrics {

    String name;
    Map<String, Quantity> usage;

    public void setUsage(Map<String, Quantity> usage) {
        if (usage == null) {
            this.usage = Collections.emptyMap();
        } else {
            this.usage = usage;
        }
    }
}
