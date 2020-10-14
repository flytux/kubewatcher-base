package com.kubeworks.watcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubeworks.watcher.config.properties.GrafanaProperties;
import com.kubeworks.watcher.config.properties.PrometheusProperties;
import com.kubeworks.watcher.config.properties.UserProperties;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.kubeworks.watcher.data.repository")
@EntityScan(basePackages = "com.kubeworks.watcher.data.entity")
@EnableTransactionManagement
@EnableFeignClients
@EnableConfigurationProperties(value = {UserProperties.class, GrafanaProperties.class, PrometheusProperties.class})
@SpringBootApplication
public class KubeWatcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(KubeWatcherApplication.class, args);
    }

    @Slf4j
    @Configuration
    @AllArgsConstructor(onConstructor_ = {@Autowired})
    static class UtilityClassInitializer {

        private final ObjectMapper objectMapper;

        @PostConstruct
        public void initialize() {
            log.debug("");
            ExternalConstants.setObjectMapper(objectMapper);
        }
    }

}