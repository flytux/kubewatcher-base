package com.kubeworks.watcher.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "application.properties.dependencies.grafana")
public class GrafanaProperties {

    private String url;
    private String apiToken;
    private String loginCookieName;

}
