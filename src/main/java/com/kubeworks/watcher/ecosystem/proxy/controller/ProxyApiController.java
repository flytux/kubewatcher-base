package com.kubeworks.watcher.ecosystem.proxy.controller;

import com.kubeworks.watcher.ecosystem.proxy.service.ProxyApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
