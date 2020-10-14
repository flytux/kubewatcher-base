package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CronJobDescribe {

    @Builder
    private CronJobDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                           Map<String, String> labels, Map<String, String> annotations, String schedule,
                           Boolean suspend, int active, DateTime lastSchedule, List<String> podTemplate,
                           List<PodTable> pods, List<EventTable> events) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.schedule = schedule;
        this.suspend = suspend;
        this.active = active;
        this.lastSchedule = lastSchedule;
        this.podTemplate = podTemplate;
        this.pods = pods;
        this.events = events;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;

    String schedule;
    Boolean suspend;
    int active;
    DateTime lastSchedule;

    List<String> podTemplate;

    List<PodTable> pods;
    List<EventTable> events;

}
