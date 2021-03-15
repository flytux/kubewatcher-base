package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.AlertRule;
import com.kubeworks.watcher.data.entity.AlertRuleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    Optional<AlertRule> findByAlertRuleId(AlertRuleId alertRuleId);

}
