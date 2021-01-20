package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.KwUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KwUserRepository extends JpaRepository<KwUser, String> {
    List<KwUser> findAllBy();
}