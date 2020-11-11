package com.kubeworks.watcher.cloud.container.controller.accessControl;

import com.kubeworks.watcher.ecosystem.kubernetes.dto.*;
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

    @GetMapping(value = "/cluster/acl/service-accounts", produces = MediaType.TEXT_HTML_VALUE)
    public String serviceAccounts(Model model) {
        List<ServiceAccountTable> serviceAccounts = accessControlRestController.serviceAccounts();
        model.addAttribute("serviceAccounts", serviceAccounts);
        return "cluster/access-control/service-accounts";
    }

    @GetMapping(value = "/cluster/acl/role-bindings", produces = MediaType.TEXT_HTML_VALUE)
    public String roleBindings(Model model) {
        List<RoleBindingTable> roleBindings = accessControlRestController.roleBindings();
        model.addAttribute("roleBindings", roleBindings);
        return "cluster/access-control/role-bindings";
    }


    /*
        클러스터 엑세스 컨트롤 메인 화면 : /cluster/acl/roles
    */
    @GetMapping(value = "/cluster/acl/roles", produces = MediaType.TEXT_HTML_VALUE)
    public String roles(Model model) {
        List<RoleTable> roles = accessControlRestController.roles();
        model.addAttribute("roles", roles);
        return "cluster/access-control/roles";
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
