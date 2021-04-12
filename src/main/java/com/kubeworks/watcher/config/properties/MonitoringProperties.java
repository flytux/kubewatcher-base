package com.kubeworks.watcher.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "application.properties.monitoring")
public class MonitoringProperties {

    String defaultClusterName;
    Map<String, ClusterConfig> clusters;

    public ClusterConfig getDefaultCluster() {
        if (StringUtils.isEmpty(defaultClusterName)) {
            return clusters.values().stream()
                .findFirst().orElseThrow(() -> new IllegalStateException("empty cluster info"));
        }
        return clusters.get(defaultClusterName);
    }

    public String getDefaultPrometheusUrl() {
        ClusterConfig defaultCluster = getDefaultCluster();
        return defaultCluster.getPrometheus().getUrl();
    }

    public String getDefaultK8sUrl() {
        ClusterConfig defaultCluster = getDefaultCluster();
        return defaultCluster.getK8s().getUrl();
    }

    public String getDefaultK8sApiToken() {
        ClusterConfig defaultCluster = getDefaultCluster();
        return defaultCluster.getK8s().getApiToken();
    }

    public String getDefaultLokiUrl(){
        ClusterConfig defaultCluster = getDefaultCluster();
        return defaultCluster.getLoki().getUrl();
    }

    @Getter @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ClusterConfig {
        String clusterName;
        PrometheusConfig prometheus;
        LokiConfig loki;
        K8s k8s;
    }

    @Getter @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PrometheusConfig {
        boolean proxy;
        String url;
    }

    @Getter @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class LokiConfig {
        boolean proxy;
        String url;
    }

    @Getter @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class K8s {
        String url;
        String apiToken;
    }
}
