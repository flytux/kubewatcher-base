package com.kubeworks.watcher.ecosystem.prometheus.feign;

import com.kubeworks.watcher.config.RestClientsConfig;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.prometheus.dto.PrometheusApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "prometheus", url = "${application.properties.monitoring.clusters.${application.properties.monitoring.default-cluster-name}.prometheus.url}",
    configuration = RestClientsConfig.BaseFeignClientConfig.class)
public interface PrometheusFeginClient {

    @GetMapping(value = ExternalConstants.PROMETHEUS_QUERY_API_URI + "{query}")
    PrometheusApiResponse getQuery(@PathVariable("query") String query);

    @GetMapping(value = ExternalConstants.PROMETHEUS_RANGE_QUERY_API_URI + "{query}")
    PrometheusApiResponse getQueryRange(@PathVariable("query")  String query,
                                        @RequestParam("start") long start,
                                        @RequestParam("end") long end,
                                        @RequestParam("step") long step);

}
