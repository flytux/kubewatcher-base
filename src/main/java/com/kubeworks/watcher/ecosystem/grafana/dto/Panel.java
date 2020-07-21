package com.kubeworks.watcher.ecosystem.grafana.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter @Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Panel {

    int id;
    String title;
    String type;
    List<PanelQuery> targets;
    List<Panel> panels;

    public String getPanelUrl() {
        // grafana embeded queryString 형식 : orgId=1&refresh=1m&from=1594872449901&to=1594874249901&var-Node=All&panelId=6
        return "?panelId=" + id + "&refresh=1m&theme=light";
    }
}
