package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.KwUserRoleRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KwUserRoleRuleRepository extends JpaRepository<KwUserRoleRule, String> {

    List<KwUserRoleRule> findAllBy();
}