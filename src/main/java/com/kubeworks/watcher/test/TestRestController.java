package com.kubeworks.watcher.test;

import com.kubeworks.watcher.ecosystem.grafana.dto.Dashboard;
import com.kubeworks.watcher.ecosystem.grafana.dto.DashboardDetail;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class TestRestController {

    private final GrafanaSerivce grafanaSerivce;

    @GetMapping(value = "/grafana/dashboards/{uid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DashboardDetail grafanaDashboardPanels(@PathVariable String uid) {
        return grafanaSerivce.dashboard(uid);
    }

}

