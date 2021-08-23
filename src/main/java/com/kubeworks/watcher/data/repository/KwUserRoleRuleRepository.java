package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.KwUserRoleRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KwUserRoleRuleRepository extends JpaRepository<KwUserRoleRule, String> {

    List<KwUserRoleRule> findAllBy();

    @Query(value="SELECT kurr.rule_name FROM kw_user_role_rule kurr", nativeQuery=true)
    List<String> findByName();

    KwUserRoleRule findByRulename(String rulename);
}
