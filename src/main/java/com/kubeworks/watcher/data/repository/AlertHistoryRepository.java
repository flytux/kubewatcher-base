package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
}
