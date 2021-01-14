package com.kubeworks.watcher.ecosystem.loki.service.impl;

import com.kubeworks.watcher.ecosystem.loki.fegin.LokiFeginClient;
import com.kubeworks.watcher.ecosystem.loki.service.LokiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class LokiServiceImpl implements LokiService {

    private final LokiFeginClient lokiFeginClient;

//    @Override
//    public String requestQuery(String query) {
//        System.out.println("impl requestQuery::::"+query);
//        return lokiFeginClient.getQuery(query);
//    }

    @Override
    public String requestQueryRange(String query) {
        //System.out.println("impl requestQueryRange22::::"+query);
        return lokiFeginClient.getQueryRange(query);
    }
//    @Override
//    public String requestQueryRange(String query, long startTimestamp, long endTimestamp) {
//        return lokiFeginClient.getQueryRange(query, startTimestamp, endTimestamp, 15);
//    }

}
