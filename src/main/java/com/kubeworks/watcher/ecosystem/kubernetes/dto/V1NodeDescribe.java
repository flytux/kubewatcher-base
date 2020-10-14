package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EventList;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1PodList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1NodeDescribe {

    @Builder
    private V1NodeDescribe(V1Node node, V1PodList pods, V1EventList events) {
        this.nodeName = Objects.requireNonNull(node.getMetadata()).getName();
        this.node = node;
        this.pods = pods;
        this.events = events;
    }

    String nodeName;

    V1Node node;

    V1PodList pods;

    V1EventList events;

    public List<BigDecimal> requestCpu() {
        return pods.getItems().stream().map(v1Pod -> {
            DateTime creationTimestamp = v1Pod.getMetadata().getCreationTimestamp();
            List<V1Container> containers = v1Pod.getSpec().getContainers();
            List<V1Container> initContainers = v1Pod.getSpec().getInitContainers();
            if (initContainers != null) {
                return initContainers;
            }

            return containers;
        }).filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .map(v1Container -> {
                return v1Container.getResources().getRequests();
            })
            .filter(Objects::nonNull)
            .map(requestMap -> {
                return requestMap.get("cpu").getNumber();
            }).collect(Collectors.toList());
    }

}
