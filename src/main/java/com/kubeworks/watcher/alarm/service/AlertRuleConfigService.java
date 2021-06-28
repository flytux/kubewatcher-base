package com.kubeworks.watcher.alarm.service;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.AlertRule;

import java.util.List;

public interface AlertRuleConfigService {

    List<AlertRule> alertRules();

    AlertRule alertRule(final Long ruleId);

    ApiResponse<String> registrationAlarmRule(final AlertRule alertRule);

    ApiResponse<String> updateAlarmRule(final AlertRule alertRule);
}
