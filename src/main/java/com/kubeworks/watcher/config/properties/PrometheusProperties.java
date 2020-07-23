package com.kubeworks.watcher.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "application.properties.dependencies.prometheus")
public class PrometheusProperties {

    private String url;

}
