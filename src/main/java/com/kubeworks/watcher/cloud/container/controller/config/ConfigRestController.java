package com.kubeworks.watcher.cloud.container.controller.config;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.ConfigMapDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.ConfigMapTable;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(path = "/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ConfigRestController {

    private final ConfigMapService configMapService;
    private final SecretService secretService;
    private final ResourceQuotaService resourceQuotaService;
    private final HPAService hpaService;
    private final NamespaceService namespaceService;
    private final CustomResourceService customResourceService;

    @GetMapping(value = "/cluster/config/configmaps", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConfigMapTable> configMaps() {
        return configMapService.allNamespaceConfigMapTables();
    }

    @GetMapping(value = "/cluster/config/configmaps/namespace/{namespace}/configmap/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConfigMapDescribe configMap(@PathVariable String namespace, @PathVariable String name) {
        return configMapService.configMap(namespace, name).orElse(null);
    }

}
