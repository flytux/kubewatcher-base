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

    @GetMapping(value = "/cluster/acl/namespace/{namespace}/service-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServiceAccountTable> serviceAccounts(@PathVariable String namespace) {
        return serviceAccountService.serviceAccounts(namespace);
    }

    @GetMapping(value = "/cluster/acl/service-accounts/namespace/{namespace}/service-account/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ServiceAccountDescribe serviceAccount(@PathVariable String namespace, @PathVariable String name) {
        return serviceAccountService.serviceAccount(namespace, name).orElse(null);
    }

    @GetMapping(value = "/cluster/acl/role-bindings", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleBindingTable> roleBindings() {
        return roleBindingService.allNamespaceRoleBindingTables();
    }

    @GetMapping(value = "/cluster/acl/namespace/{namespace}/role-binding", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleBindingTable> roleBindings(@PathVariable String namespace) {
        return roleBindingService.roleBindings(namespace);
    }

    @GetMapping(value = "/cluster/acl/role-bindings/namespace/{namespace}/role-binding/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RoleBindingDescribe roleBinding(@PathVariable String namespace, @PathVariable String name) {
        return roleBindingService.roleBinding(namespace, name).orElse(null);
    }

    /*
        Role 전체 리스트 조회
     */
    @GetMapping(value = "/cluster/acl/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleTable> roles() {

        return roleService.allNamespaceRoleTables();
    }
    /*
        Role namespace 리스트 조회
     */
    @GetMapping(value = "/cluster/acl/namespace/{namespace}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleTable> roles(@PathVariable String namespace) {
        return roleService.roles(namespace);
    }

    /*
        Role 네임스페이스로 상세 조회
    */
    @GetMapping(value = "/cluster/acl/roles/namespace/{namespace}/role/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RoleDescribe roleDescribe(@PathVariable String namespace, @PathVariable String roleName) {

        return roleService.role(namespace, roleName).orElse(null);
    }

    /*
        Pod Security Policies 전체 리스트 조회
    */
    @GetMapping(value = "/cluster/acl/pod-security-policies", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PodSecurityPolicyTable> podSecurityPolicies() {

        return podSecurityPoliciesService.allNamespacePodSecurityPolicyTables();
    }

    /*
        Pod Security Policies 네임스페이스로 상세 조회
    */
    @GetMapping(value = "/cluster/acl/pod-security-policies/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public PodSecurityPolicyDescribe podSecurityPolicyDescribe(@PathVariable String podPolicyName) {

        return podSecurityPoliciesService.podSecurityPolicy(podPolicyName).orElse(null);
    }
}
