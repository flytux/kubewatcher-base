package com.kubeworks.watcher.ecosystem.loki;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="loki", url="${application.properties.monitoring.clusters.${application.properties.monitoring.default-cluster-name}.loki.url}")
public interface LokiFeignClient {

    @GetMapping(value="/loki/api/v1/query_range")
    String requestQueryRange(@RequestParam("query") final String q, @RequestParam("start") final Long st, @RequestParam("end") final Long en, @RequestParam("step") final Long step);
}
