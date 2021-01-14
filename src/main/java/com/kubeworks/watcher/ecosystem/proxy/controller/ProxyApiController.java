package com.kubeworks.watcher.ecosystem.proxy.controller;

import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping(value = "/proxy/api")
public class ProxyApiController {

    private final ProxyApiService proxyApiService;

    public ProxyApiController(ProxyApiService proxyApiService) {
        this.proxyApiService = proxyApiService;
    }

    // TODO prometeus, k8s 등의 API처리를 위한 Proxy 객체
    @GetMapping
    public List<String> api(@RequestParam("query") String query) {
        return proxyApiService.labelValuesQuery(query);
    }

    @GetMapping("/api/proxy")
    @ResponseBody
    public String proxy() {
        String url = "http://localhost:8080";

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }
}


