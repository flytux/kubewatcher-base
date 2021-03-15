package com.kubeworks.watcher.alarm.service;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.AlertRule;

import java.util.List;

public interface AlertRuleConfigService {

    List<AlertRule> alertRules();

    AlertRule alertRule(long ruleId);

    ApiResponse<String> registrationAlarmRule(AlertRule alertRule);

    ApiResponse<String> updateAlarmRule(AlertRule alertRule);
}
