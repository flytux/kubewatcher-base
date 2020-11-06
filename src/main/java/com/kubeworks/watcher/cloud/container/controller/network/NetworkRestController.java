package com.kubeworks.watcher.cloud.container.controller.network;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(path = "/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})

public class NetworkRestController {

    private final ServiceKindService serviceKindService;
    private final IngressService ingressService;
    private final EndpointService endpointService;
    private final NetworkPolicyService networkPolicyService;

    @GetMapping(value = "/cluster/network/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServiceTable> services() {
        return serviceKindService.allNamespaceServiceTables();
    }

    @GetMapping(value = "/cluster/network/ingress", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IngressTable> ingresses() { return ingressService.allNamespaceIngressTables();
    }

    @GetMapping(value = "/cluster/network/endpoints", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EndpointTable> endpoints() { return endpointService.allNamespaceEndpointTables();
    }

    @GetMapping(value = "/cluster/network/policies", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NetworkPolicyTable> policies() { return networkPolicyService.allNamespaceNetworkPolicyTables();
    }


}
