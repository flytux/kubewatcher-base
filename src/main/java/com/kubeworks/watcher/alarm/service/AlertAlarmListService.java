package com.kubeworks.watcher.alarm.service;

import com.kubeworks.watcher.base.ApiResponse;

import java.util.Map;

public interface AlertAlarmListService {

    Map<String, Object> alertPageHistory(Integer pageNumber, String startDate, String endDate, String severity, String category, String resource, String target, int pagePostCount);

    Map<String, Object> getPageList(Integer pageNumber, Long totalCount);

    Map<String, Object> alertSearchHistory(String startDate, String endDate, String severity, String category, String resource, String system);

    ApiResponse<String> alertCheck(Long historyId);
}
