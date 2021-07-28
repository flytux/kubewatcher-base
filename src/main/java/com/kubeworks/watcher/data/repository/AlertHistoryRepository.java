package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.AlertHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
    Page<AlertHistory> findAll(Specification<AlertHistory> spec, Pageable pageable);
}
