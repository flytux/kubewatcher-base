package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobDescribe {

    @Builder
    private JobDescribe(String name, String namespace, String uid,
                        DateTime creationTimestamp, Map<String, String> labels, Map<String, String> annotations,
                        Map<String, String> selector, Integer parallelism, Integer completions,
                        Integer backoffLimit, DateTime startTime, DateTime completionTime,
                        Duration duration, String jobStatus, List<String> podTemplate,
                        List<PodTable> pods, List<EventTable> events, Map<String, String> templateLabels) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.selector = selector;
        this.parallelism = parallelism;
        this.completions = completions;
        this.backoffLimit = backoffLimit;
        this.startTime = startTime;
        this.completionTime = completionTime;
        this.duration = duration;
        this.jobStatus = jobStatus;
        this.podTemplate = podTemplate;
        this.pods = pods;
        this.events = events;
        this.templateLabels = templateLabels;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;
    Map<String, String> selector;

    Integer parallelism;
    Integer completions;
    Integer backoffLimit;

    DateTime startTime;
    DateTime completionTime;

    Duration duration;
    String jobStatus;
    List<String> podTemplate;

    List<PodTable> pods;
    List<EventTable> events;

    Map<String, String> templateLabels;

}
