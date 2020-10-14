package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class NodeTable {

    @Builder
    private NodeTable(String name, String status, String roles, String age, String version, String internalIp, String externalIp, String osImage, String kernelVersion, String containerRuntime) {
        this.name = name;
        this.status = status;
        this.roles = roles;
        this.age = age;
        this.version = version;
        this.internalIp = internalIp;
        this.externalIp = externalIp;
        this.osImage = osImage;
        this.kernelVersion = kernelVersion;
        this.containerRuntime = containerRuntime;
    }

    String name;
    String status;
    String roles;
    String age;
    String version;
    String internalIp;
    String externalIp;
    String osImage;
    String kernelVersion;
    String containerRuntime;

}
