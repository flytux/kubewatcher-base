package com.kubeworks.watcher.test;

import com.kubeworks.watcher.ecosystem.grafana.dto.Dashboard;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import com.kubeworks.watcher.ecosystem.grafana.service.impl.GrafanaSerivceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class PromChartController {

    private final GrafanaSerivce grafanaSerivce;

    @GetMapping(value = "/prom/chart")
    public String grafanaDashboards(Model model) {
        List<Dashboard> dashBoards = grafanaSerivce.dashboards();
        model.addAttribute("dashboards", dashBoards);
      
        return "chart-dashboard";
    }

}