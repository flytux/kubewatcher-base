package com.kubeworks.watcher.cloud.monitoring.controller;


import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class LogController {

    private final ProxyApiService proxyApiService;
    private final MonitoringRestController monitoringRestController;



    @GetMapping(value = "/monitoring/logging", produces = MediaType.TEXT_HTML_VALUE)
    public String logging(Model model){
        Map<String, Object> response = monitoringRestController.logging();
        model.addAllAttributes(response);
        return "monitoring/logging/logging";
    }

    @RequestMapping(value = "/apiCall")
    public Map<String, String> lokiApiCall(){
        Map<String, String> response = new HashMap<>();
        response.put("applicationValue", "ok");
        return response;
    }

//    @GetMapping(value = "/api/test")
//    @ResponseBody
//    public String callhttpapi(@RequestParam String param){
//
//
//        String part = "{pod=\"loki-0\"} |= \"error\" ";
//        String url = apiHost + "/loki/api/v1/query_range?query="+encodeURI(part);
//
//
//        RestTemplate restTemplate = new RestTemplate();
//        return restTemplate.getForObject(url, String.class);
//    }

}
