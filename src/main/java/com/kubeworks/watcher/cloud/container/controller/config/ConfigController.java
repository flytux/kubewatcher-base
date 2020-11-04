package com.kubeworks.watcher.cloud.container.controller.config;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class ConfigController {

    private final ConfigRestController configRestController;


    @GetMapping(value = "/cluster/config/configmaps", produces = MediaType.TEXT_HTML_VALUE)
    public String configMaps(Model model) {
        List<ConfigMapTable> configMaps = configRestController.configMaps();
        model.addAttribute("configMaps", configMaps);
        return "cluster/config/configmaps";
    }

    @GetMapping(value = "/cluster/config/configmaps/namespace/{namespace}/configmap/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String configMap(Model model, @PathVariable String namespace, @PathVariable String name) {
        ConfigMapDescribe configMapDescribe = configRestController.configMap(namespace, name);
        model.addAttribute("configMap", configMapDescribe);
        return "cluster/config/configmaps :: modalContents";
    }

    @GetMapping(value = "/cluster/config/secrets", produces = MediaType.TEXT_HTML_VALUE)
    public String secrets(Model model) {
        List<SecretTable> secrets = configRestController.secrets();
        model.addAttribute("secrets", secrets);
        return "cluster/config/secrets";
    }

    @GetMapping(value = "/cluster/config/secrets/namespace/{namespace}/secret/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String secret(Model model, @PathVariable String namespace, @PathVariable String name) {
        SecretDescribe secretDescribe = configRestController.secret(namespace, name);
        model.addAttribute("secret", secretDescribe);
        return "cluster/config/secrets :: modalContents";
    }

    @GetMapping(value = "/cluster/config/resource-quotas", produces = MediaType.TEXT_HTML_VALUE)
    public String resourceQuotas(Model model) {
        List<ResourceQuotaTable> resourceQuotas = configRestController.resourceQuotas();
        model.addAttribute("resourceQuotas", resourceQuotas);
        return "cluster/config/resource-quotas";
    }

    @GetMapping(value = "/cluster/config/resource-quotas/namespace/{namespace}/resourceQuota/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String resourceQuota(Model model, @PathVariable String namespace, @PathVariable String name) {
        ResourceQuotaDescribe resourceQuotaDescribe = configRestController.resourceQuota(namespace, name);
        model.addAttribute("resourceQuota", resourceQuotaDescribe);
        return "cluster/config/resource-quotas :: modalContents";
    }

    @GetMapping(value = "/cluster/config/hpa", produces = MediaType.TEXT_HTML_VALUE)
    public String hpa(Model model) {
        List<HPATable> hpa = configRestController.hpa();
        model.addAttribute("hpa", hpa);
        return "cluster/config/hpa";
    }

    @GetMapping(value = "/cluster/config/hpa/namespace/{namespace}/hpas/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String hpas(Model model, @PathVariable String namespace, @PathVariable String name) {
        HPADescribe hpaDescribe = configRestController.hpas(namespace, name);
        model.addAttribute("hpas", hpaDescribe);
        return "cluster/config/hpa :: modalContents";
    }

    @GetMapping(value = "/cluster/config/namespaces", produces = MediaType.TEXT_HTML_VALUE)
    public String namespaces(Model model) {
        List<NamespaceTable> namespaces = configRestController.namespaces();
        model.addAttribute("namespaces", namespaces);
        return "cluster/config/namespaces";
    }

    @GetMapping(value = "/cluster/config/namespaces/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String namespace(Model model, @PathVariable String name) {
        NamespaceDescribe namespaceDescribe = configRestController.namespace(name);
        model.addAttribute("namespace", namespaceDescribe);
        return "cluster/config/namespaces :: modalContents";
    }

    @GetMapping(value = "/cluster/config/custom-resources", produces = MediaType.TEXT_HTML_VALUE)
    public String customResources(Model model) {
        List<CustomResourceTable> customResources = configRestController.customResources();
        model.addAttribute("customResources", customResources);
        return "cluster/config/custom-resources";
    }

    @GetMapping(value = "/cluster/config/custom-resources/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String customResource(Model model, @PathVariable String name) {
        CustomResourceDescribe customResourceDescribe = configRestController.customResource(name);
        model.addAttribute("customResource", customResourceDescribe);
        return "cluster/config/custom-resources :: modalContents";
    }



}
