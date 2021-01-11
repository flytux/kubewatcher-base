package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import java.util.HashMap;
import java.util.Map;

public class NodeMetrics implements KubernetesObject {
    private V1ObjectMeta metadata = new V1ObjectMeta();
    private String kind;
    private String apiVersion;
    private String timestamp;
    private String window;
    private Map<String, Quantity> usage = new HashMap();

    public NodeMetrics() {
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

    public String getKind() {
        return this.kind;
    }

    public String getApiVersion() {
        return this.apiVersion;
    }

    public Map<String, Quantity> getUsage() {
        return this.usage;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
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

    public void setUsage(Map<String, Quantity> usage) {
        this.usage = usage;
    }
}
