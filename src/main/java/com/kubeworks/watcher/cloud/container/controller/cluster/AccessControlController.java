package com.kubeworks.watcher.cloud.container.controller.cluster;

import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.ecosystem.kubernetes.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/cluster/acl")
@AllArgsConstructor(onConstructor_={@Autowired})
public class AccessControlController implements BaseController {

    private static final String VIEW_ROLES = "roles";
    private static final String VIEW_ROLE_BINDINGS = "role-bindings";
    private static final String VIEW_SERVICE_ACCOUNTS = "service-accounts";
    private static final String VIEW_POD_SEC_POLICIES = "pod-security-policies";

    private final RoleService rs;
    private final NamespaceService ns;
    private final RoleBindingService rbs;
    private final ServiceAccountService sas;
    private final PodSecurityPoliciesService psps;

    @GetMapping(value="/service-accounts")
    public String serviceAccounts(final Model model) {
        model.addAttribute(Props.NAMESPACES, ns.allNamespaceTables()).addAttribute("serviceAccounts", sas.allNamespaceServiceAccountTables());
        return createViewName(VIEW_SERVICE_ACCOUNTS);
    }

    @GetMapping(value="/namespace/{namespace}/service-accounts", produces=MediaType.APPLICATION_JSON_VALUE)
    public String serviceAccounts(final Model model, @PathVariable final String namespace) {
        model.addAttribute("serviceAccounts", sas.serviceAccounts(namespace));
        return createViewName(VIEW_SERVICE_ACCOUNTS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/service-accounts/namespace/{namespace}/service-account/{name}")
    public String serviceAccount(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("serviceAccount", sas.serviceAccount(namespace, name).orElse(null));
        return createViewName(VIEW_SERVICE_ACCOUNTS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/role-bindings")
    public String roleBindings(final Model model) {
        model.addAttribute(Props.NAMESPACES, ns.allNamespaceTables()).addAttribute("roleBindings", rbs.allNamespaceRoleBindingTables());
        return createViewName(VIEW_ROLE_BINDINGS);
    }

    @GetMapping(value="/namespace/{namespace}/role-bindings", produces=MediaType.APPLICATION_JSON_VALUE)
    public String roleBindings(final Model model, @PathVariable final String namespace) {
        model.addAttribute("roleBindings", rbs.roleBindings(namespace));
        return createViewName(VIEW_ROLE_BINDINGS, Props.CONTENT_LIST);
    }

    @GetMapping(value="/role-bindings/namespace/{namespace}/role-binding/{name}")
    public String roleBinding(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("roleBinding", rbs.roleBinding(namespace, name).orElse(null));
        return createViewName(VIEW_ROLE_BINDINGS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/roles")
    public String roles(final Model model) {
        model.addAttribute(Props.NAMESPACES, ns.allNamespaceTables()).addAttribute(VIEW_ROLES, rs.allNamespaceRoleTables());
        return createViewName(VIEW_ROLES);
    }

    @GetMapping(value="/namespace/{namespace}/roles", produces=MediaType.APPLICATION_JSON_VALUE)
    public String roles(final Model model, @PathVariable final String namespace) {
        model.addAttribute(VIEW_ROLES, rs.roles(namespace));
        return createViewName(VIEW_ROLES, Props.CONTENT_LIST);
    }

    @GetMapping(value="/roles/namespace/{namespace}/role/{name}")
    public String role(final Model model, @PathVariable final String namespace, @PathVariable final String name) {
        model.addAttribute("roleDescribe", rs.role(namespace, name).orElse(null));
        return createViewName(VIEW_ROLES, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/pod-security-policies")
    public String podSecurityPolicies(final Model model) {
        model.addAttribute("podSecurityPolicies", psps.allNamespacePodSecurityPolicyTables());
        return createViewName(VIEW_POD_SEC_POLICIES);
    }

    @GetMapping(value="/pod-security-policies/{name}")
    public String podSecurityPolicy(final Model model, @PathVariable final String name) {
        model.addAttribute("policyDescribe", psps.podSecurityPolicy(name).orElse(null));
        return createViewName(VIEW_POD_SEC_POLICIES, Props.MODAL_CONTENTS);
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "cluster/access-control/";
    }
}
