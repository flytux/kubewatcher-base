package com.kubeworks.watcher.ecosystem.kubernetes.service;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.SecretDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.SecretTable;

import java.util.List;
import java.util.Optional;

public interface SecretService {

    List<SecretTable> allNamespaceSecretTables();

    List<SecretTable> secrets(String namespace);

    Optional<SecretDescribe> secret(String namespace, String name);

    List<SecretDescribe> secretTable(String namespace);
}
