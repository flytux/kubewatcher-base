package com.kubeworks.watcher.ecosystem.proxy.service;

import com.kubeworks.watcher.data.entity.PageVariable;

import java.util.List;

public interface ProxyApiService {

    String singleValueByQuery(String query);

    List<String> labelValuesQuery(String query);

    List<String> multiValuesQuery(String query, String metricName);

    List<String> query(PageVariable variable);
}
