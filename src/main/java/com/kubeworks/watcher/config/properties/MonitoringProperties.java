package com.kubeworks.watcher.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter @Setter
@ConfigurationProperties(prefix="application.properties.monitoring")
public class MonitoringProperties {

    private String defaultClusterName;
    private Map<String, ClusterConfig> clusters;

    public ClusterConfig getDefaultCluster() {

        if (StringUtils.isEmpty(defaultClusterName)) {
            return clusters.values().stream().findFirst().orElseThrow(() -> new IllegalStateException("empty cluster info"));
        }

        return clusters.get(defaultClusterName);
    }

    public String getDefaultPrometheusUrl() {
        return getDefaultCluster().getPrometheus().getUrl();
    }

    public String getDefaultK8sUrl() {
        return getDefaultCluster().getK8s().getUrl();
    }

    public String getDefaultK8sApiToken() {
        return getDefaultCluster().getK8s().getApiToken();
    }

    @Getter @Setter
    public static class ClusterConfig {
        private String clusterName;
        private PrometheusConfig prometheus;
        private LokiConfig loki;
        private K8s k8s;
    }

    @Getter @Setter
    public static class PrometheusConfig {
        private boolean proxy;
        private String url;
    }

    @Getter @Setter
    public static class LokiConfig {
        private boolean proxy;
        private String url;
    }

    @Getter @Setter
    public static class K8s {
        private String url;
        private String apiToken;
    }
}
