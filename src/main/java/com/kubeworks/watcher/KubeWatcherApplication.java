package com.kubeworks.watcher;

import com.kubeworks.watcher.config.properties.GrafanaProperties;
import com.kubeworks.watcher.config.properties.PrometheusProperties;
import com.kubeworks.watcher.config.properties.UserProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableConfigurationProperties(value = {UserProperties.class, GrafanaProperties.class, PrometheusProperties.class})
@SpringBootApplication
public class KubeWatcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(KubeWatcherApplication.class, args);
    }

}
