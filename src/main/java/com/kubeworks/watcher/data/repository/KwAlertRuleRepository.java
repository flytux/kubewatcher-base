package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.ChartQuery;
import com.kubeworks.watcher.data.entity.KwAlertRule;
import com.kubeworks.watcher.data.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KwAlertRuleRepository extends JpaRepository<KwAlertRule, Long> {
    List<KwAlertRule> findAllBy();

}
