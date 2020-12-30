package com.kubeworks.watcher.cloud.container.controller.config;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.preference.service.PageConstants;
import com.kubeworks.watcher.preference.service.PageViewService;
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

    private static final long CONFIGMAP_MENU_ID = 300;
    private static final long SECRET_MENU_ID = 301;
    private static final long RESOURCEQUOTA_MENU_ID = 302;
    private static final long HPA_MENU_ID = 303;
    private static final long NAMESPACE_MENU_ID = 304;
    private static final long CUSTOMRESOURCE_MENU_ID = 305;

    private final PageViewService pageViewService;
    private final ConfigRestController configRestController;

    @GetMapping(value = "/cluster/config/configmaps", produces = MediaType.TEXT_HTML_VALUE)
    public String configMaps(Model model) {
        List<ConfigMapTable> configMaps = configRestController.configMaps();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("configMaps", configMaps);
        model.addAttribute("page", pageViewService.getPageView(CONFIGMAP_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_CONFIGMAPS);
        return "cluster/config/configmaps";
    }

    @GetMapping(value = "/cluster/config/namespace/{namespace}/configmaps", produces = MediaType.APPLICATION_JSON_VALUE)
    public String configMaps(Model model, @PathVariable String namespace) {
        List<ConfigMapTable> configMaps = configRestController.configMaps(namespace);
        model.addAttribute("configMaps", configMaps);
        return "cluster/config/configmaps :: contentList";
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
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("secrets", secrets);
        model.addAttribute("page", pageViewService.getPageView(SECRET_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_SECRETS);
        return "cluster/config/secrets";
    }

    @GetMapping(value = "/cluster/config/namespace/{namespace}/secrets", produces = MediaType.APPLICATION_JSON_VALUE)
    public String secrets(Model model, @PathVariable String namespace) {
        List<SecretTable> secrets = configRestController.secrets(namespace);
        model.addAttribute("secrets", secrets);
        return "cluster/config/secrets :: contentList";
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
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("resourceQuotas", resourceQuotas);
        model.addAttribute("page", pageViewService.getPageView(RESOURCEQUOTA_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_RESOURCEQUOTAS);
        return "cluster/config/resource-quotas";
    }

    @GetMapping(value = "/cluster/config/namespace/{namespace}/resource-quotas", produces = MediaType.APPLICATION_JSON_VALUE)
    public String resourceQuotas(Model model, @PathVariable String namespace) {
        List<ResourceQuotaTable> resourceQuotas = configRestController.resourceQuotas(namespace);
        model.addAttribute("resourceQuotas", resourceQuotas);
        return "cluster/config/resource-quotas :: contentList";
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
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("hpa", hpa);
        model.addAttribute("page", pageViewService.getPageView(HPA_MENU_ID));
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_HPA);
        return "cluster/config/hpa";
    }

    @GetMapping(value = "/cluster/config/namespace/{namespace}/hpa", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hpa(Model model, @PathVariable String namespace) {
        List<HPATable> hpa = configRestController.hpa(namespace);
        model.addAttribute("hpa", hpa);
        return "cluster/config/hpa :: contentList";
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
        model.addAttribute("page", pageViewService.getPageView(NAMESPACE_MENU_ID));
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
        model.addAttribute("page", pageViewService.getPageView(CUSTOMRESOURCE_MENU_ID));
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
