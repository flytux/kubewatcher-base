package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.LimitRangeDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.LimitRangeTable;

import java.util.List;
import java.util.Optional;

public interface LimitRangeService {

    List<LimitRangeTable> allNamespaceLimitRangeTables();

    List<LimitRangeDescribe> listNamespacedLimitRange(String namespace);

    Optional<LimitRangeDescribe> limitRange(String namespace, String name);

}
