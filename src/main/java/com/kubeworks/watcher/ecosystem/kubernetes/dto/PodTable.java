package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PodTable {

    String name;
    String namespace;
    String ready;
    String status;
    String restarts;
    String age;
    String ip;
    String node;
    String nominatedNode;
    String readinessGates;

}
