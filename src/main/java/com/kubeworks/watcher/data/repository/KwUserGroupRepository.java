package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.KwUserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KwUserGroupRepository extends JpaRepository<KwUserGroup, String> {
    List<KwUserGroup> findAllBy();
}
