package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.cloud.monitoring.service.PageMetricService;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.data.entity.Page;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor(onConstructor_={@Autowired})
@RequestMapping(value="/monitoring/application")
public class ApplicationController implements BaseController {

    private static final long APPLICATION_OVERVIEW_MENU_ID = 100;

    private final MonitoringProperties properties;
    private final PageMetricService<Page> service;

    @GetMapping(value="/overview")
    public String application(final Model model) {

        model.addAttribute(Props.HOST, properties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, service.pageMetrics(APPLICATION_OVERVIEW_MENU_ID));

        return createViewName("overview");
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "monitoring/application/";
    }
}
