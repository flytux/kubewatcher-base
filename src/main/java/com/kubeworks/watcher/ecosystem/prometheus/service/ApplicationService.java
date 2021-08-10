package com.kubeworks.watcher.ecosystem.prometheus.service;

import com.kubeworks.watcher.data.entity.ApplicationManagement;
import com.kubeworks.watcher.data.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ApplicationService {

    List<ApplicationManagement> getApplicationManagementList();
    String getServiceNamesOfPromQL();
    String getServiceNamesLoki();
    List<String> getNamespaces();
    Map<String, List<ApplicationManagement>> getManagementByNamespace();
    List<String> getManagementByName();
    ApplicationManagement getUnknownBoard();
    String getDisplayName(final String name);
    Map<String, ApplicationManagement> getServiceMap();

    @Component
    class ApplicationManagementHandler {

        private static final String CACHE_NAME = "app-names";

        private final CacheManager manager;
        private final ApplicationRepository repo;

        @Autowired
        public ApplicationManagementHandler(final CacheManager manager, final ApplicationRepository repo) {
            this.repo = repo;
            this.manager = manager;
        }

        @Cacheable(key="#root.methodName", value=CACHE_NAME, unless="#result == null")
        public List<ApplicationManagement> retrieve() {
            return repo.findAll();
        }

        public void clearCache() {
            Optional.ofNullable(manager.getCache(CACHE_NAME)).ifPresent(Cache::invalidate);
        }
    }
}
