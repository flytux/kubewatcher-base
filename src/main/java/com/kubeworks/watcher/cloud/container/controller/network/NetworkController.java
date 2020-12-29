package com.kubeworks.watcher.cloud.container.controller.network;

import com.kubeworks.watcher.cloud.container.controller.config.ConfigRestController;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.preference.service.PageConstants;
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
public class NetworkController {

    private final NetworkRestController networkRestController;
    private final ConfigRestController configRestController;

    @GetMapping(value = "/cluster/network/services", produces = MediaType.TEXT_HTML_VALUE)
    public String services(Model model) {
        List<ServiceTable> services = networkRestController.services();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("services", services);
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_SERVICES);
        return "cluster/network/services";
    }

    @GetMapping(value = "/cluster/network/namespace/{namespace}/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public String services(Model model, @PathVariable String namespace) {
        List<ServiceTable> services = networkRestController.services(namespace);
        model.addAttribute("services", services);
        return "cluster/network/services :: contentList";
    }

    @GetMapping(value = "/cluster/network/services/namespace/{namespace}/service/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String service(Model model, @PathVariable String namespace, @PathVariable String name) {
        ServiceDescribe serviceDescribe = networkRestController.service(namespace, name);
        model.addAttribute("service", serviceDescribe);
        return "cluster/network/services :: modalContents";
    }

    @GetMapping(value = "/cluster/network/ingress", produces = MediaType.TEXT_HTML_VALUE)
    public String ingresses(Model model) {
        List<IngressTable> ingresses = networkRestController.ingresses();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("ingresses", ingresses);
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_INGRESS);
        return "cluster/network/ingress";
    }

    @GetMapping(value = "/cluster/network/namespace/{namespace}/ingress", produces = MediaType.APPLICATION_JSON_VALUE)
    public String ingresses(Model model, @PathVariable String namespace) {
        List<IngressTable> ingresses = networkRestController.ingresses(namespace);
        model.addAttribute("ingresses", ingresses);
        return "cluster/network/ingress :: contentList";
    }

    @GetMapping(value = "/cluster/network/ingress/namespace/{namespace}/ingresses/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String ingress(Model model, @PathVariable String namespace, @PathVariable String name) {
        IngressDescribe ingressDescribe = networkRestController.ingress(namespace, name);
        model.addAttribute("ingress", ingressDescribe);
        return "cluster/network/ingress :: modalContents";
    }

    @GetMapping(value = "/cluster/network/endpoints", produces = MediaType.TEXT_HTML_VALUE)
    public String endpoints(Model model) {
        List<EndpointTable> endpoints = networkRestController.endpoints();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("endpoints", endpoints);
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_ENDPOINTS);
        return "cluster/network/endpoints";
    }

    @GetMapping(value = "/cluster/network/namespace/{namespace}/endpoints", produces = MediaType.APPLICATION_JSON_VALUE)
    public String endpoints(Model model, @PathVariable String namespace) {
        List<EndpointTable> endpoints = networkRestController.endpoints(namespace);
        model.addAttribute("endpoints", endpoints);
        return "cluster/network/endpoints :: contentList";
    }

    @GetMapping(value = "/cluster/network/endpoints/namespace/{namespace}/endpoint/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String endpoint(Model model, @PathVariable String namespace, @PathVariable String name) {
        EndpointDescribe endpointDescribe = networkRestController.endpoint(namespace, name);
        model.addAttribute("endpoint", endpointDescribe);
        return "cluster/network/endpoints :: modalContents";
    }

    @GetMapping(value = "/cluster/network/policies", produces = MediaType.TEXT_HTML_VALUE)
    public String policies(Model model) {
        List<NetworkPolicyTable> policies = networkRestController.policies();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("policies", policies);
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_POLICIES);
        return "cluster/network/policies";
    }

    @GetMapping(value = "/cluster/network/namespace/{namespace}/policies", produces = MediaType.APPLICATION_JSON_VALUE)
    public String policies(Model model, @PathVariable String namespace) {
        List<NetworkPolicyTable> policies = networkRestController.policies(namespace);
        model.addAttribute("policies", policies);
        return "cluster/network/policies :: contentList";
    }

    @GetMapping(value = "/cluster/network/policies/namespace/{namespace}/policy/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String policy(Model model, @PathVariable String namespace, @PathVariable String name) {
        NetworkPolicyDescribe networkPolicyDescribe = networkRestController.policy(namespace, name);
        model.addAttribute("policy", networkPolicyDescribe);
        return "cluster/network/policies :: modalContents";
    }



}
