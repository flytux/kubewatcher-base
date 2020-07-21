package com.kubeworks.watcher.ecosystem.grafana.service;

import com.kubeworks.watcher.ecosystem.grafana.dto.Dashboard;
import com.kubeworks.watcher.ecosystem.grafana.dto.DashboardDetail;
import com.kubeworks.watcher.ecosystem.grafana.dto.Panel;

import java.util.List;

public interface GrafanaSerivce {

    List<Dashboard> dashboards();

    DashboardDetail dashboard(String uid);

    Panel panel(String dashboardUid, String panelUid);
}
