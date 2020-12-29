package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.JobDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.JobTable;

import java.util.List;
import java.util.Optional;

public interface JobService {

    List<JobTable> allNamespaceJobTables();

    List<JobTable> jobs(String namespace);

    Optional<JobDescribe> job(String namespace, String name);

}
