package com.kubeworks.watcher.config.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "application.properties")
public class ApplicationServiceProperties {

    Service unknownService;
    List<Service> services = Collections.emptyList();
    List<String> namespaces = Collections.emptyList();
    Map<String, List<Service>> serviceByNamespace = Collections.emptyMap();


    public Map<String, Service> getServiceMap() {
        return services.stream().collect(Collectors.toConcurrentMap(Service::getName, service -> service));
    }

    public void setServices(List<Service> services) {
        this.services = services;
        this.namespaces = services.stream().map(Service::getNamespace).distinct().collect(Collectors.toList());
        this.serviceByNamespace = services.stream().collect(Collectors.groupingBy(Service::getNamespace));

        this.unknownService = new Service();
        this.unknownService.setName(ExternalConstants.UNKNOWN);
        this.unknownService.setDisplayName(ExternalConstants.UNKNOWN_DASH);
    }

    @JsonIgnore
    public String getServiceNamesOfPromQL() {
        return services.stream().map(Service::getName).collect(Collectors.joining(".*|","", ".*"));
    }

    @JsonIgnore
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
