package com.kubeworks.watcher.ecosystem.grafana.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.stream.Collectors;

@Getter @Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Dashboard {

    long id;
    String uid;
    String title;
    String uri;
    String url;
    String slug;
    String type;
    Set<String> tags;
    boolean starred;

    long folderId;
    String folderUid;
    String folderTitle;
    String folderUrl;

    List<Panel> panels;
    Map<String, TemplateVariable> templating;

    public void setTemplating(Map<String, List<TemplateVariable>> templating) {
        this.templating = templating.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(TemplateVariable::getName, templateVariable -> templateVariable));
    }
}
