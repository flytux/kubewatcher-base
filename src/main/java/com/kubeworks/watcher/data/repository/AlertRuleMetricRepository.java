package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.AlertRuleId;
import com.kubeworks.watcher.data.entity.AlertRuleMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRuleMetricRepository extends JpaRepository<AlertRuleMetric, AlertRuleId> {
}
