package com.kubeworks.watcher.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application.properties.monitoring.clusters.local.loki.url")
public class LokiUrlProperties {

    private String url;
}
