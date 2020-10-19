package com.kubeworks.watcher.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "application.properties.dependencies.k8s")
public class K8sProperties {

    private String name;
    private String url;
    private String apiToken;

}
