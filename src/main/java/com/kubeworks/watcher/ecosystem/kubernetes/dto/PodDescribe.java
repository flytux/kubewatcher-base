package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ContainerExtends;
import io.kubernetes.client.openapi.models.V1KeyToPath;
import io.kubernetes.client.openapi.models.V1PodCondition;
import io.kubernetes.client.openapi.models.V1Toleration;
import io.kubernetes.client.openapi.models.V1Volume;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PodDescribe {

    @Builder
    private PodDescribe(String name, String namespace, String uid, Map<String, String> labels, Map<String, String> annotations,
                        String status, DateTime creationTimestamp, String node, String podIp,
                        String priority, List<V1PodCondition> conditions, List<V1Toleration> tolerations, List<V1ContainerExtends> containers,
                        List<V1Volume> volumes, List<EventTable> events) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.labels = labels;
        this.annotations = annotations;
        this.status = status;
        this.creationTimestamp = creationTimestamp;
        this.node = node;
        this.podIp = podIp;
        this.priority = priority;
        this.conditions = conditions;
        this.tolerations = tolerations;
        this.containers = containers;
        this.volumes = volumes;
        this.events = events;
    }

    String name;
    String namespace;
    String uid;
    Map<String, String> labels;
    Map<String, String> annotations;
    String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    String node;
    String podIp;
    String priority;
    List<V1PodCondition> conditions;
    List<V1Toleration> tolerations;

    List<V1ContainerExtends> containers;
    List<V1Volume> volumes;

    List<EventTable> events;

    public List<PodDescribeVolume> getVolumes() {
        return volumes.stream().map(PodDescribe::convertVolumes)
            .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static PodDescribeVolume convertVolumes(V1Volume v1Volume) {
        PodDescribeVolume.PodDescribeVolumeBuilder builder = PodDescribeVolume.builder();
        builder.name(v1Volume.getName());

        if (v1Volume.getNfs() != null) {
            builder.type("NFS");
            builder.values(Collections.singletonList(v1Volume.getNfs().getPath()));
            return builder.build();
        }

        if (v1Volume.getHostPath() != null) {
            builder.type(v1Volume.getHostPath().getType());
            builder.values(Collections.singletonList(v1Volume.getHostPath().getPath()));
            return builder.build();
        }

        if (v1Volume.getSecret() != null) {
            builder.type("Secret");
            if (v1Volume.getSecret().getItems() != null) {
                List<String> paths = v1Volume.getSecret().getItems().stream()
                    .map(V1KeyToPath::getPath).collect(Collectors.toList());
                builder.values(paths);
            }
            return builder.build();
        }

        return null;
    }
}
