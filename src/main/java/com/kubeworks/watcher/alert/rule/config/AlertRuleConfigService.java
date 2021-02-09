package com.kubeworks.watcher.alert.rule.config;

import com.kubeworks.watcher.data.entity.KwAlertRule;

import java.util.List;

public interface AlertRuleConfigService {
    List<KwAlertRule> getAlertRuleList();

}
