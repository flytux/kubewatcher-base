package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.ApplicationManagement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<ApplicationManagement, String> {

}