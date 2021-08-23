package com.kubeworks.watcher.cloud.container.controller.network;

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
@RequestMapping(value="/cluster/network")
@AllArgsConstructor(onConstructor_={@Autowired})
public class NetworkController implements BaseController {

    private static final long SERVICE_MENU_ID = 310;
    private static final long INGRESS_MENU_ID = 311;
    private static final long ENDPOINT_MENU_ID = 312;
    private static final long POLICY_MENU_ID = 313;

    private static final String VIEW_SERVICES = "services";
    private static final String VIEW_INGRESS = "ingress";
    private static final String VIEW_ENDPOINTS = "endpoints";
    private static final String VIEW_POLICIES = "policies";

    private final PageViewService pageViewService;

    private final ServiceKindService serviceKindService;
    private final IngressService ingressService;
    private final EndpointService endpointService;
    private final NetworkPolicyService networkPolicyService;

    private final NamespaceService namespaceService;

    @GetMapping(value="/services")
    public String services(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_SERVICES);
        model.addAttribute(Props.PAGE, pageViewService.getPageView(SERVICE_MENU_ID));
        model.addAttribute(VIEW_SERVICES, serviceKindService.allNamespaceServiceTables());

        return createViewName(VIEW_SERVICES);
    }

    @GetMapping(value="/namespace/{namespace}/services", produces=MediaType.APPLICATION_JSON_VALUE)
    public String services(final Model model, @PathVariable final String namespace) {
        model.addAttribute(VIEW_SERVICES, serviceKindService.services(namespace));
        return createViewName(VIEW_SERVICES, Props.CONTENT_LIST);
    }

    @GetMapping(value="/services/namespace/{namespace}/service/{name}")
    public String service(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("service", serviceKindService.service(namespace, name).orElse(null));
        return createViewName(VIEW_SERVICES, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/ingress")
    public String ingresses(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_INGRESS);
        model.addAttribute(Props.PAGE, pageViewService.getPageView(INGRESS_MENU_ID));
        model.addAttribute("ingresses", ingressService.allNamespaceIngressTables());

        return createViewName(VIEW_INGRESS);
    }

    @GetMapping(value="/namespace/{namespace}/ingress", produces=MediaType.APPLICATION_JSON_VALUE)
    public String ingresses(final Model model, @PathVariable final String namespace) {
        model.addAttribute("ingresses", ingressService.ingresses(namespace));
        return createViewName(VIEW_INGRESS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/ingress/namespace/{namespace}/ingresses/{name}")
    public String ingress(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute(VIEW_INGRESS, ingressService.ingress(namespace, name).orElse(null));
        return createViewName(VIEW_INGRESS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/endpoints")
    public String endpoints(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_ENDPOINTS);
        model.addAttribute(Props.PAGE, pageViewService.getPageView(ENDPOINT_MENU_ID));
        model.addAttribute(VIEW_ENDPOINTS, endpointService.allNamespaceEndpointTables());

        return createViewName(VIEW_ENDPOINTS);
    }

    @GetMapping(value="/namespace/{namespace}/endpoints", produces=MediaType.APPLICATION_JSON_VALUE)
    public String endpoints(final Model model, @PathVariable final String namespace) {
        model.addAttribute(VIEW_ENDPOINTS, endpointService.endpoints(namespace));
        return createViewName(VIEW_ENDPOINTS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/endpoints/namespace/{namespace}/endpoint/{name}")
    public String endpoint(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("endpoint", endpointService.endpoint(namespace, name).orElse(null));
        return createViewName(VIEW_ENDPOINTS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/policies")
    public String policies(final Model model) {

        model.addAttribute(Props.NAMESPACES, namespaceService.allNamespaceTables());
        model.addAttribute(Props.LINK, PageConstants.API_URL_BY_NAMESPACED_POLICIES);
        model.addAttribute(Props.PAGE, pageViewService.getPageView(POLICY_MENU_ID));
        model.addAttribute(VIEW_POLICIES, networkPolicyService.allNamespaceNetworkPolicyTables());

        return createViewName(VIEW_POLICIES);
    }

    @GetMapping(value="/namespace/{namespace}/policies", produces=MediaType.APPLICATION_JSON_VALUE)
    public String policies(final Model model, @PathVariable final String namespace) {
        model.addAttribute(VIEW_POLICIES, networkPolicyService.policies(namespace));
        return createViewName(VIEW_POLICIES, Props.CONTENT_LIST);
    }

    @GetMapping(value="/policies/namespace/{namespace}/policy/{name}")
    public String policy(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("policy", networkPolicyService.networkPolicy(namespace, name).orElse(null));
        return createViewName(VIEW_POLICIES, Props.MODAL_CONTENTS);
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "cluster/network/";
    }
}
