package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor(onConstructor_={@Autowired})
public class MainDashboardController implements BaseController {

    private static final long MAIN_MENU_ID = 99;

    private final MonitoringProperties properties;
    private final PageViewService pageViewService;

    @GetMapping(value="/main")
    public String main(final Model model) {

        model.addAttribute(Props.HOST, properties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(MAIN_MENU_ID));

        return createViewName("main");
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "";
    }
}
