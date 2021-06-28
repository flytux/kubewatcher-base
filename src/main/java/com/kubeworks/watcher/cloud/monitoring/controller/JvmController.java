package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/monitoring/jvm")
@AllArgsConstructor(onConstructor_={@Autowired})
public class JvmController implements BaseController {

    private static final long JVM_DETAIL_MENU_ID = 121;
    private static final long JVM_OVERVIEW_MENU_ID = 120;

    private final PageViewService service;
    private final MonitoringProperties properties;

    @GetMapping(value="/overview")
    public String overview(final Model model) {

        model.addAttribute(Props.HOST, properties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, service.getPageView(JVM_OVERVIEW_MENU_ID));

        return createViewName("overview");
    }

    @GetMapping(value="/application")
    public String detail(final Model model) {

        model.addAttribute(Props.HOST, properties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, service.getPageView(JVM_DETAIL_MENU_ID));

        return createViewName("application");
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "monitoring/jvm/";
    }
}
