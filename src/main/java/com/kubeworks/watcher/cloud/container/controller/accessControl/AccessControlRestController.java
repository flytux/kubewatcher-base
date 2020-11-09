package com.kubeworks.watcher.cloud.container.controller.accessControl;

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

public class AccessControlRestController {

    private final ServiceAccountService serviceAccountService;
    private final RoleBindingService roleBindingService;
    private final RoleService roleService;
    private final PodSecurityPoliciesService podSecurityPoliciesService;

    @GetMapping(value = "/cluster/acl/service-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServiceAccountTable> serviceAccounts() {
        return serviceAccountService.allNamespaceServiceAccountTables();
    }

    @GetMapping(value = "/cluster/acl/role-bindings", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleBindingTable> roleBindings() {

        return roleBindingService.allNamespaceRoleBindingTables();
    }

    /*
        Role 전체 리스트 조회
     */
    @GetMapping(value = "/cluster/acl/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleTable> roles() {

        return roleService.allNamespaceRoleTables();
    }

    /*
        Role 네임스페이스로 상세 조회
    */
    @GetMapping(value = "/cluster/acl/roles/{namespace}/role/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RoleDescribe roleDescribe(@PathVariable String namespace, @PathVariable String roleName) {

        return roleService.role(namespace, roleName).orElse(null);
    }

    /*
        Pod Security Policies 전체 리스트 조회
    */
    @GetMapping(value = "/cluster/acl/podsecuriypolicies", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PodSecurityPolicyTable> podSecurityPolicies() {

        return podSecurityPoliciesService.allNamespacePodSecurityPolicyTables();
    }

    /*
        Pod Security Policies 네임스페이스로 상세 조회
    */
    @GetMapping(value = "/cluster/acl/podsecuriypolicies/{namespace}/podsecuriypolicy/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PodSecurityPolicyDescribe podSecurityPolicyDescribe(@PathVariable String namespace, @PathVariable String podPolicyName) {

        return podSecurityPoliciesService.podSecurityPolicy(podPolicyName).orElse(null);
    }
}
