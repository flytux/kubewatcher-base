package com.kubeworks.watcher.cloud.container.controller.config;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
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

    @GetMapping(value = "/cluster/config/namespace/{namespace}/configmaps", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ConfigMapTable> configMaps(@PathVariable String namespace) {
        return configMapService.configMaps(namespace);
    }

    @GetMapping(value = "/cluster/config/configmaps/namespace/{namespace}/configmap/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConfigMapDescribe configMap(@PathVariable String namespace, @PathVariable String name) {
        return configMapService.configMap(namespace, name).orElse(null);
    }

    @GetMapping(value = "/cluster/config/secrets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SecretTable> secrets() {
        return secretService.allNamespaceSecretTables();
    }

    @GetMapping(value = "/cluster/config/namespace/{namespace}/secrets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SecretTable> secrets(@PathVariable String namespace) {
        return secretService.secrets(namespace);
    }

    @GetMapping(value = "/cluster/config/secrets/namespace/{namespace}/secret/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SecretDescribe secret(@PathVariable String namespace, @PathVariable String name) {
        return secretService.secret(namespace, name).orElse(null);
    }

    @GetMapping(value = "/cluster/config/resource-quotas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ResourceQuotaTable> resourceQuotas() {
        return resourceQuotaService.allNamespaceResourceQuotaTables();
    }

    @GetMapping(value = "/cluster/config/namespace/{namespace}/resource-quotas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ResourceQuotaTable> resourceQuotas(@PathVariable String namespace) {
        return resourceQuotaService.resourceQuotas(namespace);
    }

    @GetMapping(value = "/cluster/config/resource-quotas/namespace/{namespace}/resourceQuota/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResourceQuotaDescribe resourceQuota(@PathVariable String namespace, @PathVariable String name) {
        return resourceQuotaService.resourceQuota(namespace, name).orElse(null);
    }

    @GetMapping(value = "/cluster/config/hpa", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HPATable> hpa() {
        return hpaService.allNamespaceHPATables();
    }

    @GetMapping(value = "/cluster/config/namespace/{namespace}/hpa", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HPATable> hpa(@PathVariable String namespace) {
        return hpaService.hpa(namespace);
    }

    @GetMapping(value = "/cluster/config/hpa/namespace/{namespace}/hpas/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public HPADescribe hpas(@PathVariable String namespace, @PathVariable String name) {
        return hpaService.hpa(namespace, name).orElse(null);
    }

    @GetMapping(value = "/cluster/config/namespaces", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NamespaceTable> namespaces() {
        return namespaceService.allNamespaceTables();
    }

    @GetMapping(value = "/cluster/config/namespaces/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NamespaceDescribe namespace(@PathVariable String name) {
        return namespaceService.namespace(name).orElse(null);
    }

    @GetMapping(value = "/cluster/config/custom-resources", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomResourceTable> customResources() {
        return customResourceService.allCustomResourceTables();
    }

    @GetMapping(value = "/cluster/config/custom-resources/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomResourceDescribe customResource(@PathVariable String name) {
        return customResourceService.customResource(name).orElse(null);
    }

}
