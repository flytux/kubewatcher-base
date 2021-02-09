package com.kubeworks.watcher.alert.rule.config.service;

import com.kubeworks.watcher.alert.rule.config.AlertRuleConfigService;
import com.kubeworks.watcher.data.entity.KwAlertRule;
import com.kubeworks.watcher.data.repository.KwAlertRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AlertRuleConfigSerivceImpl implements AlertRuleConfigService {
    private final KwAlertRuleRepository kwAlertRuleRepository;

    @Autowired
    public AlertRuleConfigSerivceImpl(KwAlertRuleRepository kwAlertRuleRepository) {
        this.kwAlertRuleRepository = kwAlertRuleRepository;
    }


    public List<KwAlertRule> getAlertRuleList() {
        return kwAlertRuleRepository.findAllBy();
    }
}
