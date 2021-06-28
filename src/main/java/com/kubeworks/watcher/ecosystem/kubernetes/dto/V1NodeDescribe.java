package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import io.kubernetes.client.openapi.models.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class V1NodeDescribe {

    @Builder
    private V1NodeDescribe(final V1Node node, final V1PodList pods, final V1EventList events) {
        this.node = node; this.pods = pods; this.events = events;
        this.nodeName = Objects.requireNonNull(node.getMetadata()).getName();
    }

    private String nodeName;
    private V1Node node;
    private V1PodList pods;
    private V1EventList events;

    // TODO 코드수정함
    public List<BigDecimal> requestCpu() {

        return pods.getItems().stream()
            .map(V1Pod::getSpec).filter(Objects::nonNull)
            .map(s -> Optional.ofNullable(s.getInitContainers()).orElseGet(s::getContainers))
            .filter(Objects::nonNull).flatMap(Collection::stream)
            .map(V1Container::getResources).filter(Objects::nonNull)
            .map(V1ResourceRequirements::getRequests).filter(Objects::nonNull)
            .map(rm -> rm.get("cpu").getNumber()).collect(Collectors.toList());
    }
}
