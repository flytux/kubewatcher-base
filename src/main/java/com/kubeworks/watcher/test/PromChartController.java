package com.kubeworks.watcher.test;

import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.ecosystem.grafana.dto.Dashboard;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import com.kubeworks.watcher.preference.service.PageViewService;
import com.kubeworks.watcher.preference.service.impl.PageViewServiceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class PromChartController {

    private final GrafanaSerivce grafanaSerivce;
    private final PageViewService pageViewService;

    @GetMapping(value = "/prom/chart")
    public String grafanaDashboards(Model model) {
        List<Dashboard> dashBoards = grafanaSerivce.dashboards();
        model.addAttribute("dashboards", dashBoards);
      
        return "chart-dashboard";
    }

    @GetMapping(value = "/prom/overview")
    public String pageList(Model model) {
        List<Page> pages = pageViewService.getPageList();
        model.addAttribute("pages", pages);

        return "prom-chart";
    }

    @GetMapping(value = "/prom/edit")
    public String doNot(Model model) {

        return "edit-query";
    }
}