package com.kubeworks.watcher.ecosystem.grafana.dto;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class PanelQuery {


    String expr;
    String format;
    boolean instant;
    boolean hide;
    int step;

    public String getApiQuery() {
        return instant ? ExternalConstants.PROMETHEUS_QUERY_API_URI + expr
            : ExternalConstants.PROMETHEUS_RANGE_QUERY_API_URI + expr;
    }
}
