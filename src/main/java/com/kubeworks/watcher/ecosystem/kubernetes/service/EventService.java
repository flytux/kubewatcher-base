package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.EventDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EventTable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1EventTableList;

import java.util.List;
import java.util.Optional;

public interface EventService {

    List<EventTable> allNamespaceEventTables();

    List<EventTable> events(String namespace);

    Optional<EventDescribe> event(String namespace, String name);

    Optional<V1EventTableList> eventTable(String kind, String namespace, String name, String uId);
}
