package com.kubeworks.watcher.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "application.properties")
public class ApplicationServiceProperties {

    List<Service> services;

    public String getServiceNamesOfPromQL() {
        return services.stream().map(Service::getName).collect(Collectors.joining("|"));
    }

    public String getDisplayName(String name) {
        return services.stream()
            .filter(service -> StringUtils.equalsIgnoreCase(name, service.getName()))
            .map(Service::getDisplayName)
            .findFirst().orElse("Unknown");
    }

    @Getter @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Service {

        String code;
        String name;
        String namespace;
        String displayName;

    }

}
