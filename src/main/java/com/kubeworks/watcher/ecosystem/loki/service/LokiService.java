package com.kubeworks.watcher.ecosystem.loki.service;


public interface LokiService {


//    String requestQuery(String query);

    //LokiResponse requestQueryRange(String query, long startTimestamp, long endTimestamp);
    String requestQueryRange(String query);

}
