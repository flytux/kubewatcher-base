package com.kubeworks.watcher.cloud.container.controller.network;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class NetworkController {

    private final NetworkRestController networkRestController;

    @GetMapping(value = "/cluster/network/services", produces = MediaType.TEXT_HTML_VALUE)
    public String services(Model model) {
        List<ServiceTable> services = networkRestController.services();
        model.addAttribute("services", services);
        return "cluster/network/services";
    }

    @GetMapping(value = "/cluster/network/ingress", produces = MediaType.TEXT_HTML_VALUE)
    public String ingresses(Model model) {
        List<IngressTable> ingresses = networkRestController.ingresses();
        model.addAttribute("ingresses", ingresses);
        return "cluster/network/ingress";
    }

    @GetMapping(value = "/cluster/network/endpoints", produces = MediaType.TEXT_HTML_VALUE)
    public String endpoints(Model model) {
        List<EndpointTable> endpoints = networkRestController.endpoints();
        model.addAttribute("endpoints", endpoints);
        return "cluster/network/endpoints";
    }

    @GetMapping(value = "/cluster/network/policies", produces = MediaType.TEXT_HTML_VALUE)
    public String policies(Model model) {
        List<NetworkPolicyTable> policies = networkRestController.policies();
        model.addAttribute("policies", policies);
        return "cluster/network/policies";
    }


//    @GetMapping(value = "/cluster/network/services/namespace/{namespace}/service/{name}", produces = MediaType.TEXT_HTML_VALUE)
//    public String service(Model model, @PathVariable String namespace, @PathVariable String name) {
//        ServiceDescribe serviceDescribe = networkRestController.service(namespace, name);
//        model.addAttribute("service", ServiceDescribe);
//        return "cluster/network/services :: modalContents";
//    }
}
