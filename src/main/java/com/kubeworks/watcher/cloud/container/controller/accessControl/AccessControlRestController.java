package com.kubeworks.watcher.cloud.container.controller.accessControl;

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

public class AccessControlRestController {

    private final ServiceAccountService serviceAccountService;
    private final RoleBindingService roleBindingService;

    @GetMapping(value = "/cluster/acl/service-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServiceAccountTable> serviceAccounts() {
        return serviceAccountService.allNamespaceServiceAccountTables();
    }

    @GetMapping(value = "/cluster/acl/role-bindings", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RoleBindingTable> roleBindings() {

        return roleBindingService.allNamespaceRoleBindingTables();
    }
}
