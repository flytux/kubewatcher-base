package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ApplicationController {

    private final MonitoringRestController monitoringRestController;
    private final ProxyApiService proxyApiService;

    @GetMapping(value = "/monitoring/application/overview", produces = MediaType.TEXT_HTML_VALUE)
    public String application(Model model) {
        Map<String, Object> response = monitoringRestController.application();
        model.addAllAttributes(response);
        return "monitoring/application/overview";
    }

}
