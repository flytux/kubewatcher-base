package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.KwUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KwUserRoleRepository extends JpaRepository<KwUserRole, String> {
    List<KwUserRole> findAllBy();

    @Query(value="SELECT DISTINCT kw.rolename FROM Kw_User_Role kw",nativeQuery = true)
    List<String> findDistinctAllBy();
}