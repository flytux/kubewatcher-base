package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PodMetrics implements KubernetesObject {

    String apiVersion;
    String kind;
    V1ObjectMeta metadata;
    String timestamp;
    String window;
    List<ContainerMetrics> containers;

    public void setContainers(List<ContainerMetrics> containers) {
        if (containers == null) {
            this.containers = Collections.emptyList();
        } else {
            this.containers = containers;
        }
    }
}
