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
public class JobTable {

    String name;
    String namespace;
    String completions;
    String duration;
    String age;
    String containers;
    String images;
    String selector;
    String statusCondition;

}
