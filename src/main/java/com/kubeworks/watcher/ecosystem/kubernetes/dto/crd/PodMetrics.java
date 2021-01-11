package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE)
public class PodMetrics implements KubernetesObject {

    private String apiVersion;
    private String kind;
    private V1ObjectMeta metadata;
    private String timestamp;
    private String window;
    private List<ContainerMetrics> containers;

    public PodMetrics() {
    }

    public String getApiVersion() {
        return this.apiVersion;
    }

    public String getKind() {
        return this.kind;
    }

    public V1ObjectMeta getMetadata() {
        return this.metadata;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getWindow() {
        return this.window;
    }

    public List<ContainerMetrics> getContainers() {
        return this.containers;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setMetadata(V1ObjectMeta metadata) {
        this.metadata = metadata;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setWindow(String window) {
        this.window = window;
    }

    public void setContainers(List<ContainerMetrics> containers) {
        this.containers = containers;
    }

}
