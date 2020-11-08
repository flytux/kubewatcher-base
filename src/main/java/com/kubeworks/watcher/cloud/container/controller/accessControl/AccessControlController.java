package com.kubeworks.watcher.cloud.container.controller.accessControl;

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
}
