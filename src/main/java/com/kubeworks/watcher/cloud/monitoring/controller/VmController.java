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
@RequestMapping(value="/monitoring/vm")
@AllArgsConstructor(onConstructor_={@Autowired})
public class VmController implements BaseController {

    private static final long VM_DETAIL_MENU_ID = 141;
    private static final long VM_OVERVIEW_MENU_ID = 140;

    private final PageViewService pageViewService;
    private final MonitoringProperties properties;

    @GetMapping(value="/overview")
    public String overview(final Model model) {

        model.addAttribute(Props.HOST, properties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(VM_OVERVIEW_MENU_ID));

        return createViewName("vm-overview");
    }

    @GetMapping(value="/monitoring")
    public String detail(final Model model) {

        model.addAttribute(Props.HOST, properties.getDefaultPrometheusUrl());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(VM_DETAIL_MENU_ID));

        return createViewName("vm-monitoring");
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "monitoring/vm/";
    }
}
