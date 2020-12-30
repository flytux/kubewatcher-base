package com.kubeworks.watcher.cloud.container.controller.accessControl;

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
public class AccessControlController {

    private final AccessControlRestController accessControlRestController;
    private final ConfigRestController configRestController;

    @GetMapping(value = "/cluster/acl/service-accounts", produces = MediaType.TEXT_HTML_VALUE)
    public String serviceAccounts(Model model) {
        List<ServiceAccountTable> serviceAccounts = accessControlRestController.serviceAccounts();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("serviceAccounts", serviceAccounts);
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_SERVICEACCOUNTS);
        return "cluster/access-control/service-accounts";
    }

    @GetMapping(value = "/cluster/acl/namespace/{namespace}/service-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public String serviceAccounts(Model model, @PathVariable String namespace) {
        List<ServiceAccountTable> serviceAccounts = accessControlRestController.serviceAccounts(namespace);
        model.addAttribute("serviceAccounts", serviceAccounts);
        return "cluster/access-control/service-accounts :: contentList";
    }

    @GetMapping(value = "/cluster/acl/service-accounts/namespace/{namespace}/service-account/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String serviceAccount(Model model, @PathVariable String namespace, @PathVariable String name) {
        ServiceAccountDescribe serviceAccountDescribe = accessControlRestController.serviceAccount(namespace, name);
        model.addAttribute("serviceAccount", serviceAccountDescribe);
        return "cluster/access-control/service-accounts :: modalContents";
    }

    @GetMapping(value = "/cluster/acl/role-bindings", produces = MediaType.TEXT_HTML_VALUE)
    public String roleBindings(Model model) {
        List<RoleBindingTable> roleBindings = accessControlRestController.roleBindings();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("roleBindings", roleBindings);
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_ROLEBINDINGS);
        return "cluster/access-control/role-bindings";
    }

    @GetMapping(value = "/cluster/acl/namespace/{namespace}/role-bindings", produces = MediaType.APPLICATION_JSON_VALUE)
    public String roleBindings(Model model, @PathVariable String namespace) {
        List<RoleBindingTable> roleBindings = accessControlRestController.roleBindings(namespace);
        model.addAttribute("roleBindings", roleBindings);
        return "cluster/access-control/role-bindings :: contentList";
    }

    @GetMapping(value = "/cluster/acl/role-bindings/namespace/{namespace}/role-binding/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String roleBinding(Model model, @PathVariable String namespace, @PathVariable String name) {
        RoleBindingDescribe roleBindingDescribe = accessControlRestController.roleBinding(namespace, name);
        model.addAttribute("roleBinding", roleBindingDescribe);
        return "cluster/access-control/role-bindings :: modalContents";
    }

    /*
        클러스터 엑세스 컨트롤 메인 화면 : /cluster/acl/roles
    */
    @GetMapping(value = "/cluster/acl/roles", produces = MediaType.TEXT_HTML_VALUE)
    public String roles(Model model) {
        List<RoleTable> roles = accessControlRestController.roles();
        List<NamespaceTable> namespaces = configRestController.namespaces();

        model.addAttribute("roles", roles);
        model.addAttribute("namespaces", namespaces);
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_ROLES);
        return "cluster/access-control/roles";
    }

    @GetMapping(value = "/cluster/acl/namespace/{namespace}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public String roles(Model model, @PathVariable String namespace) {
        List<RoleTable> roles = accessControlRestController.roles(namespace);
        model.addAttribute("roles", roles);
        return "cluster/access-control/roles :: contentList";
    }

    /*
        클러스터 엑세스 컨트롤 팝업 모달 화면 : /cluster/acl/roles/{name}
     */
    @GetMapping(value = "/cluster/acl/roles/namespace/{namespace}/role/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String role(Model model, @PathVariable String namespace, @PathVariable String name) {
        RoleDescribe roleDescribe = accessControlRestController.roleDescribe(namespace, name);
        model.addAttribute("roleDescribe", roleDescribe);
        return "cluster/access-control/roles :: modalContents";
    }

    /*
    클러스터 POD Security Policy 메인 화면 : /cluster/acl/pod-security-policies
*/
    @GetMapping(value = "/cluster/acl/pod-security-policies", produces = MediaType.TEXT_HTML_VALUE)
    public String podSecurityPolicies(Model model) {
        List<PodSecurityPolicyTable> podSecurityPolicies = accessControlRestController.podSecurityPolicies();
        model.addAttribute("podSecurityPolicies", podSecurityPolicies);
        return "cluster/access-control/pod-security-policies";
    }

    /*
        클러스터 POD Security Policy 팝업 모달 화면 : /cluster/acl/pod-security-policies/{name}
     */
    @GetMapping(value = "/cluster/acl/pod-security-policies/{name}", produces = MediaType.TEXT_HTML_VALUE)
    public String podSecurityPolicy(Model model, @PathVariable String name) {
        PodSecurityPolicyDescribe policyDescribe = accessControlRestController.podSecurityPolicyDescribe(name);
        model.addAttribute("policyDescribe", policyDescribe);
        return "cluster/access-control/pod-security-policies :: modalContents";
    }
}
