package com.kubeworks.watcher.ecosystem.proxy.service;

import java.util.List;

public interface ProxyApiService {

    String singleValueByQuery(String query);

    List<String> labelValuesQuery(String query);
}
