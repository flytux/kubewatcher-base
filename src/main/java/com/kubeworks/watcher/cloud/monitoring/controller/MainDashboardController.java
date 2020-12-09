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
public class MainDashboardController {

    private final MonitoringRestController monitoringRestController;

    @GetMapping(value = "/main", produces = MediaType.TEXT_HTML_VALUE)
    public String main(Model model) {
        Map<String, Object> response = monitoringRestController.dashboard();
        model.addAllAttributes(response);
        return "main";
    }
}
