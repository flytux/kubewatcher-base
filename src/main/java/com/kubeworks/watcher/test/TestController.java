package com.kubeworks.watcher.test;

import com.kubeworks.watcher.ecosystem.grafana.dto.Dashboard;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class TestController {

    private final GrafanaSerivce grafanaSerivce;

    @GetMapping(value = "/grafana/dashboards")
    public String grafanaDashboards(Model model) {
        List<Dashboard> dashBoards = grafanaSerivce.dashboards();
        model.addAttribute("dashboards", dashBoards);
        return "grafana-dashboard";
    }

}

