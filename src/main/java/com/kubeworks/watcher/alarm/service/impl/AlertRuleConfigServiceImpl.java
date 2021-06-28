package com.kubeworks.watcher.alarm.service.impl;

import com.kubeworks.watcher.alarm.service.AlertRuleConfigService;
import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.AlertRule;
import com.kubeworks.watcher.data.entity.AlertRuleMetric;
import com.kubeworks.watcher.data.repository.AlertRuleMetricRepository;
import com.kubeworks.watcher.data.repository.AlertRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AlertRuleConfigServiceImpl implements AlertRuleConfigService {

    private final AlertRuleRepository alertRuleRepository;
    private final AlertRuleMetricRepository alertRuleMetricRepository;

    @Autowired
    public AlertRuleConfigServiceImpl(AlertRuleRepository alertRuleRepository, AlertRuleMetricRepository alertRuleMetricRepository) {
        this.alertRuleRepository = alertRuleRepository;
        this.alertRuleMetricRepository = alertRuleMetricRepository;
    }


    public List<AlertRule> alertRules() {
        return alertRuleRepository.findAll();
    }

    @Override
    public AlertRule alertRule(final Long ruleId) {
        return alertRuleRepository.findById(ruleId).orElse(null);
    }

    @Override
    public ApiResponse<String> registrationAlarmRule(final AlertRule alertRule) {
        ApiResponse<String> response = new ApiResponse<>();

        Optional<AlertRuleMetric> alertRuleMetricOptional = alertRuleMetricRepository.findById(alertRule.getAlertRuleId());

        if (!alertRuleMetricOptional.isPresent()) {
            response.setSuccess(false);
            response.setMessage("등록하신 Role에 대한 Metric이 존재하지 않습니다.\n관리자에게 문의하세요.");
            return response;
        }

        try {
            alertRuleRepository.save(alertRule);
            response.setSuccess(true);
        } catch (DataIntegrityViolationException e) {
            log.error("failed save", e);
            response.setSuccess(false);
            response.setMessage("이미 등록된 Rule 입니다.");
        }

        return response;
    }

    @Transactional
    @Override
    public ApiResponse<String> updateAlarmRule(final AlertRule alertRule) {
        ApiResponse<String> response = new ApiResponse<>();
        Optional<AlertRule> dbAlertRuleOptional = alertRuleRepository.findById(alertRule.getRuleId());
        if (!dbAlertRuleOptional.isPresent()) {
            response.setSuccess(false);
            response.setMessage("존재하지 않는 Rule 입니다.");
            return response;
        }
        AlertRule dbAlertRule = dbAlertRuleOptional.get();
        dbAlertRule.update(alertRule);
        response.setSuccess(true);
        return response;
    }
}
