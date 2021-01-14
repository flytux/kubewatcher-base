package com.kubeworks.watcher.cloud.monitoring.controller;


import com.kubeworks.watcher.ecosystem.loki.fegin.LokiFeginClient;
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
public class LogController {

    private final ProxyApiService proxyApiService;

    private final MonitoringRestController monitoringRestController;

    private final LokiFeginClient lokiFeginClient;

    @GetMapping(value = "/monitoring/logging", produces = MediaType.TEXT_HTML_VALUE)
    public String logging(Model model){
        Map<String, Object> response = monitoringRestController.logging();
        model.addAllAttributes(response);
        return "monitoring/logging/logging";
    }


}
