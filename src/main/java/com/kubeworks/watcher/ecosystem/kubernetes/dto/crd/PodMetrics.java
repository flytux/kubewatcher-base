package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.common.collect.ImmutableList;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class PodMetrics implements KubernetesObject {

    String apiVersion;
    String kind;
    V1ObjectMeta metadata;
    String timestamp;
    String window;
    List<ContainerMetrics> containers;

    public void setContainers(final List<ContainerMetrics> containers) {
        this.containers = Objects.nonNull(containers) ? containers : ImmutableList.of();
    }
}
