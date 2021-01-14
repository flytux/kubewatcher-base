package com.kubeworks.watcher.ecosystem.loki.fegin;

import com.kubeworks.watcher.config.RestClientsConfig;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "loki", url = "${application.properties.dependencies.loki.url}",
    configuration = RestClientsConfig.BaseFeignClientConfig.class)  //url 실제 호출할 서비스의 url
public interface LokiFeginClient {

    @GetMapping(value = ExternalConstants.LOKI_QUERY_API_URI + "{query}") // /loki/api/v1/query?query= + {query} = {pod="loki-0"} |= "error"
    String getQuery(@PathVariable String query);


    @GetMapping(value = ExternalConstants.LOKI_CONTETX_PATH + "{query}")
    String getQueryRange(@PathVariable String query);

//    @GetMapping(value = ExternalConstants.LOKI_RANGE_QUERY_API_URI + "{query}")
//    String getQueryRange(@PathVariable String query,
//                                  @RequestParam("start") long start,
//                                  @RequestParam("start") long end,
//                                  @RequestParam("step") long step);

}
