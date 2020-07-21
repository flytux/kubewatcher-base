package com.kubeworks.watcher.ecosystem.grafana.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter @Setter
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
public class DashboardDetail {
    DashboardMeta meta;
    Dashboard dashboard;
}
