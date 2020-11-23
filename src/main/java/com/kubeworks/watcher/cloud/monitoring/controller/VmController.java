package com.kubeworks.watcher.cloud.monitoring.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class VmController {

    private final MonitoringRestController monitoringRestController;

    @GetMapping(value = "/monitoring/vm/overview", produces = MediaType.TEXT_HTML_VALUE)
    public String overview(Model model) {
        Map<String, Object> response = monitoringRestController.vmOverview();
        model.addAllAttributes(response);
        return "monitoring/vm/vm-overview";
    }

    @GetMapping(value = "/monitoring/vm/monitoring", produces = MediaType.TEXT_HTML_VALUE)
    public String detail(Model model) {
        Map<String, Object> response = monitoringRestController.vmDetail();
        model.addAllAttributes(response);
        return "monitoring/vm/vm-monitoring";
    }

}
