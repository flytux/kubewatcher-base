package com.kubeworks.watcher.ecosystem.proxy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/proxy/api")
public class ProxyApiController {

    // TODO prometeus, k8s 등의 API처리를 위한 Proxy 객체
    @GetMapping
    public Object api(@RequestParam("query") String query) {
        return null;
    }

}
