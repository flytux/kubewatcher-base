package com.kubeworks.watcher.cloud.container.controller.network;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EndpointService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.IngressService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.NetworkPolicyService;
import com.kubeworks.watcher.ecosystem.kubernetes.service.ServiceKindService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/cluster/network")
@AllArgsConstructor(onConstructor_={@Autowired})

public class NetworkRestController {

    private final ServiceKindService serviceKindService;
    private final IngressService ingressService;
    private final EndpointService endpointService;
    private final NetworkPolicyService networkPolicyService;

    @GetMapping(value="/services")
    public List<ServiceTable> services() {
        return serviceKindService.allNamespaceServiceTables();
    }

    @GetMapping(value="/namespace/{namespace}/services")
    public List<ServiceTable> services(@PathVariable final String namespace) {
        return serviceKindService.services(namespace);
    }

    @GetMapping(value="/services/namespace/{namespace}/service/{name}")
    public ServiceDescribe service(@PathVariable final String namespace, @PathVariable final String name) {
        return serviceKindService.service(namespace, name).orElse(null);
    }

    @GetMapping(value="/ingress")
    public List<IngressTable> ingresses() { 
        return ingressService.allNamespaceIngressTables();
    }

    @GetMapping(value="/namespace/{namespace}/ingress")
    public List<IngressTable> ingresses(@PathVariable final String namespace) {
        return ingressService.ingresses(namespace);
    }

    @GetMapping(value="/ingress/namespace/{namespace}/ingresses/{name}")
    public IngressDescribe ingress(@PathVariable final String namespace, @PathVariable final String name) {
        return ingressService.ingress(namespace, name).orElse(null);
    }

    @GetMapping(value="/endpoints")
    public List<EndpointTable> endpoints() { 
        return endpointService.allNamespaceEndpointTables();
    }

    @GetMapping(value="/namespace/{namespace}/endpoints")
    public List<EndpointTable> endpoints(@PathVariable final String namespace) {
        return endpointService.endpoints(namespace);
    }

    @GetMapping(value="/endpoints/namespace/{namespace}/endpoint/{name}")
    public EndpointDescribe endpoint(@PathVariable final String namespace, @PathVariable final String name) {
        return endpointService.endpoint(namespace, name).orElse(null);
    }

    @GetMapping(value="/policies")
    public List<NetworkPolicyTable> policies() { return networkPolicyService.allNamespaceNetworkPolicyTables();
    }

    @GetMapping(value="/namespace/{namespace}/policies")
    public List<NetworkPolicyTable> policies(@PathVariable final String namespace) {
        return networkPolicyService.policies(namespace);
    }

    @GetMapping(value="/policies/namespace/{namespace}/policy/{name}")
    public NetworkPolicyDescribe policy(@PathVariable final String namespace, @PathVariable final String name) {
        return networkPolicyService.networkPolicy(namespace, name).orElse(null);
    }
}
