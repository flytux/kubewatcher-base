package com.kubeworks.watcher.cloud.monitoring.controller.cluster;


import com.kubeworks.watcher.ecosystem.loki.service.LokiService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class LokiRestController {
    private final LokiService lokiService;

//    @GetMapping(value = "/loki/query", produces = MediaType.APPLICATION_JSON_VALUE)
//    public String lokiApiCall(@RequestParam String apiurl){
//        System.out.println("lokiApiCall::");
//        //apiurl null체크해서 없을시 대입 하는 방법으로 진행??은 아닌것같고
//        System.out.println("url::"+apiurl);
//        return lokiService.requestQuery(apiurl);
//    }

    @GetMapping(value = "/loki/query_range", produces = MediaType.APPLICATION_JSON_VALUE)
    public String lokiQueryRange(@RequestParam String query){
        return lokiService.requestQueryRange(query);
        //return lokiService.requestQueryRange(apiurl,start,end);

    }
}
