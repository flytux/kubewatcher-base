package com.kubeworks.watcher.ecosystem.proxy.service;

import com.kubeworks.watcher.data.entity.PageVariable;

import java.util.List;

public interface ProxyApiService {

    String singleValueByQuery(final String query);

    List<String> labelValuesQuery(final String query);

    List<String> multiValuesQuery(final String query, final String metricName);

    List<String> query(final PageVariable variable);
}
