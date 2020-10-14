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
public class CronJobTable {

    String name;
    String namespace;
    String schedule;
    boolean suspend;
    int active;
    String lastSchedule;
    String age;
    String containers;
    String images;
    String selector;

}
