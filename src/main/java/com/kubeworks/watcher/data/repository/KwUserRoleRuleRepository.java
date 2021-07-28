package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.KwUserRoleRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KwUserRoleRuleRepository extends JpaRepository<KwUserRoleRule, String> {

    List<KwUserRoleRule> findAllBy();

    @Query(value="SELECT kw.rule_name FROM Kw_User_Role_Rule kw",nativeQuery = true)
    List<String> findByName();

    KwUserRoleRule findByRulename(String rulename);
}
