package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.CronJobDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.CronJobTable;

import java.util.List;
import java.util.Optional;

public interface CronJobService {

    List<CronJobTable> allNamespaceCronJobTables();

    List<CronJobTable> cronJobs(String namespace);

    Optional<CronJobDescribe> cronJob(String namespace, String name);

}
