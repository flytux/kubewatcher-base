package com.kubeworks.watcher.cloud.container.controller.config;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/cluster/config")
@AllArgsConstructor(onConstructor_={@Autowired})
public class ConfigRestController {

    private final ConfigMapService configMapService;
    private final SecretService secretService;
    private final ResourceQuotaService resourceQuotaService;
    private final HPAService hpaService;
    private final NamespaceService namespaceService;
    private final CustomResourceService customResourceService;

    @GetMapping(value="/configmaps")
    public List<ConfigMapTable> configMaps() {
        return configMapService.allNamespaceConfigMapTables();
    }

    @GetMapping(value="/namespace/{namespace}/configmaps")
    public List<ConfigMapTable> configMaps(@PathVariable final String namespace) {
        return configMapService.configMaps(namespace);
    }

    @GetMapping(value="/configmaps/namespace/{namespace}/configmap/{name}")
    public ConfigMapDescribe configMap(@PathVariable final String namespace, @PathVariable final String name) {
        return configMapService.configMap(namespace, name).orElse(null);
    }

    @GetMapping(value="/secrets")
    public List<SecretTable> secrets() {
        return secretService.allNamespaceSecretTables();
    }

    @GetMapping(value="/namespace/{namespace}/secrets")
    public List<SecretTable> secrets(@PathVariable final String namespace) {
        return secretService.secrets(namespace);
    }

    @GetMapping(value="/secrets/namespace/{namespace}/secret/{name}")
    public SecretDescribe secret(@PathVariable final String namespace, @PathVariable final String name) {
        return secretService.secret(namespace, name).orElse(null);
    }

    @GetMapping(value="/resource-quotas")
    public List<ResourceQuotaTable> resourceQuotas() {
        return resourceQuotaService.allNamespaceResourceQuotaTables();
    }

    @GetMapping(value="/namespace/{namespace}/resource-quotas")
    public List<ResourceQuotaTable> resourceQuotas(@PathVariable final String namespace) {
        return resourceQuotaService.resourceQuotas(namespace);
    }

    @GetMapping(value="/resource-quotas/namespace/{namespace}/resourceQuota/{name}")
    public ResourceQuotaDescribe resourceQuota(@PathVariable final String namespace, @PathVariable final String name) {
        return resourceQuotaService.resourceQuota(namespace, name).orElse(null);
    }

    @GetMapping(value="/hpa")
    public List<HPATable> hpa() {
        return hpaService.allNamespaceHPATables();
    }

    @GetMapping(value="/namespace/{namespace}/hpa")
    public List<HPATable> hpa(@PathVariable final String namespace) {
        return hpaService.hpa(namespace);
    }

    @GetMapping(value="/hpa/namespace/{namespace}/hpas/{name}")
    public HPADescribe hpas(@PathVariable final String namespace, @PathVariable final String name) {
        return hpaService.hpa(namespace, name).orElse(null);
    }

    @GetMapping(value="/namespaces")
    public List<NamespaceTable> namespaces() {
        return namespaceService.allNamespaceTables();
    }

    @GetMapping(value="/namespaces/{name}")
    public NamespaceDescribe namespace(@PathVariable final String name) {
        return namespaceService.namespace(name).orElse(null);
    }

    @GetMapping(value="/custom-resources")
    public List<CustomResourceTable> customResources() {
        return customResourceService.allCustomResourceTables();
    }

    @GetMapping(value="/custom-resources/{name}")
    public CustomResourceDescribe customResource(@PathVariable final String name) {
        return customResourceService.customResource(name).orElse(null);
    }
}
