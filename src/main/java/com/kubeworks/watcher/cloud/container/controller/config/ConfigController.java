package com.kubeworks.watcher.cloud.container.controller.config;

import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;
import com.kubeworks.watcher.preference.service.PageConstants;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/cluster/config")
@AllArgsConstructor(onConstructor_={@Autowired})
public class ConfigController implements BaseController {

    private static final long HPA_MENU_ID = 303;
    private static final long SECRET_MENU_ID = 301;
    private static final long CONFIGMAP_MENU_ID = 300;
    private static final long NAMESPACE_MENU_ID = 304;
    private static final long RESOURCE_QUOTA_MENU_ID = 302;
    private static final long CUSTOM_RESOURCE_MENU_ID = 305;

    private static final String VIEW_HPA = "hpa";
    private static final String VIEW_SECRETS = "secrets";
    private static final String VIEW_CONFIGMAPS = "configmaps";
    private static final String VIEW_RESOURCE_QUOTAS = "resource_quotas";
    private static final String VIEW_CUSTOM_RESOURCES = "custom-resources";

    private final PageViewService pageViewService;

    private final ConfigMapService configMapService;
    private final SecretService secretService;
    private final ResourceQuotaService resourceQuotaService;
    private final HPAService hpaService;
    private final NamespaceService namespaceService;
    private final CustomResourceService customResourceService;

    @GetMapping(value="/configmaps")
    public String configMaps(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_CONFIGMAPS);
        model.addAttribute(Props.PAGE, pageViewService.getPageView(CONFIGMAP_MENU_ID));
        model.addAttribute("configMaps", configMapService.allNamespaceConfigMapTables());

        return createViewName(VIEW_CONFIGMAPS);
    }

    @GetMapping(value="/namespace/{namespace}/configmaps", produces=MediaType.APPLICATION_JSON_VALUE)
    public String configMaps(final Model model, @PathVariable final String namespace) {
        model.addAttribute("configMaps", configMapService.configMaps(namespace));
        return createViewName(VIEW_CONFIGMAPS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/configmaps/namespace/{namespace}/configmap/{name}")
    public String configMap(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("configMap", configMapService.configMap(namespace, name).orElse(null));
        return createViewName(VIEW_CONFIGMAPS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/secrets")
    public String secrets(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_SECRETS);
        model.addAttribute(Props.PAGE, pageViewService.getPageView(SECRET_MENU_ID));
        model.addAttribute(VIEW_SECRETS, secretService.allNamespaceSecretTables());

        return createViewName(VIEW_SECRETS);
    }

    @GetMapping(value="/namespace/{namespace}/secrets", produces=MediaType.APPLICATION_JSON_VALUE)
    public String secrets(final Model model, @PathVariable final String namespace) {
        model.addAttribute(VIEW_SECRETS, secretService.secrets(namespace));
        return createViewName(VIEW_SECRETS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/secrets/namespace/{namespace}/secret/{name}")
    public String secret(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("secret", secretService.secret(namespace, name).orElse(null));
        return createViewName(VIEW_SECRETS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/resource-quotas")
    public String resourceQuotas(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_RESOURCEQUOTAS);
        model.addAttribute(Props.PAGE, pageViewService.getPageView(RESOURCE_QUOTA_MENU_ID));
        model.addAttribute("resourceQuotas", resourceQuotaService.allNamespaceResourceQuotaTables());

        return createViewName(VIEW_RESOURCE_QUOTAS);
    }

    @GetMapping(value="/namespace/{namespace}/resource-quotas", produces=MediaType.APPLICATION_JSON_VALUE)
    public String resourceQuotas(final Model model, @PathVariable final String namespace) {
        model.addAttribute("resourceQuotas", resourceQuotaService.resourceQuotas(namespace));
        return createViewName(VIEW_RESOURCE_QUOTAS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/resource-quotas/namespace/{namespace}/resourceQuota/{name}")
    public String resourceQuota(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("resourceQuota", resourceQuotaService.resourceQuota(namespace, name).orElse(null));
        return createViewName(VIEW_RESOURCE_QUOTAS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/hpa")
    public String hpa(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_HPA);
        model.addAttribute(Props.PAGE, pageViewService.getPageView(HPA_MENU_ID));
        model.addAttribute("hpa", hpaService.allNamespaceHPATables());

        return createViewName(VIEW_HPA);
    }

    @GetMapping(value="/namespace/{namespace}/hpa", produces=MediaType.APPLICATION_JSON_VALUE)
    public String hpa(final Model model, @PathVariable final String namespace) {
        model.addAttribute("hpa", hpaService.hpa(namespace));
        return createViewName(VIEW_HPA, Props.CONTENT_LIST);
    }

    @GetMapping(value="/hpa/namespace/{namespace}/hpas/{name}")
    public String hpas(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("hpas", hpaService.hpa(namespace, name).orElse(null));
        return createViewName(VIEW_HPA, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/namespaces")
    public String namespaces(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(NAMESPACE_MENU_ID));

        return createViewName(Props.NAMESPACES);
    }

    @GetMapping(value="/namespaces/{name}")
    public String namespace(final Model model, @PathVariable final String name) {
        model.addAttribute("namespace", namespaceService.namespace(name).orElse(null));
        return createViewName(Props.NAMESPACES, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/custom-resources")
    public String customResources(final Model model) {

        model.addAttribute(Props.PAGE, pageViewService.getPageView(CUSTOM_RESOURCE_MENU_ID));
        model.addAttribute("customResources", customResourceService.allCustomResourceTables());

        return createViewName(VIEW_CUSTOM_RESOURCES);
    }

    @GetMapping(value="/custom-resources/{name}")
    public String customResource(final Model model, @PathVariable final String name) {
        model.addAttribute("customResource", customResourceService.customResource(name).orElse(null));
        return createViewName(VIEW_CUSTOM_RESOURCES, Props.MODAL_CONTENTS);
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "cluster/config/";
    }
}
