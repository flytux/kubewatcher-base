package com.kubeworks.watcher.user.controller;

import com.kubeworks.watcher.data.entity.*;
import com.kubeworks.watcher.data.repository.KwUserRepository;
import com.kubeworks.watcher.data.repository.KwUserRoleRepository;
import com.kubeworks.watcher.data.repository.KwUserRoleRuleRepository;
import com.kubeworks.watcher.user.service.KwGroupService;
import com.kubeworks.watcher.user.service.KwRoleService;
import com.kubeworks.watcher.user.service.KwUserService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class kwUserRestController {

    private final KwUserService kwUserService;
    private final KwGroupService kwGroupService;
    private final KwRoleService kwRoleService;
    private final KwUserRepository kwUserRepository;
    private final KwUserRoleRuleRepository kwUserRoleRuleRepository;

    @ResponseBody
    @RequestMapping("/security/groups/save")
    public KwUserGroup saveGroup(@ModelAttribute @Validated KwUserGroup kwUserGroup, BindingResult bindingResult) {
        System.out.println("error:" + bindingResult.hasErrors());
        // TODO BindingResult
        if(bindingResult.hasErrors()) {
            return null;
        }
        KwUserGroup saveGroup = kwGroupService.saveGroup(kwUserGroup);

        return saveGroup;
    }

    @ResponseBody
    @RequestMapping("/security/groups/{groupname}/delete")
    public KwUserGroup deleteGroup(@ModelAttribute KwUserGroup kwUserGroup) {
        return kwGroupService.deleteGroup(kwUserGroup);
    }

    @ResponseBody
    @RequestMapping(value="/security/users/modify", method= RequestMethod.POST)
    public KwUser modifyUser(@ModelAttribute("user") KwUser kwUser, @RequestParam("groupList") String groupList,
                             @RequestParam("roleList") List<String> roleList, Model model) {

        KwUser dbKwUser = kwUserRepository.findById(kwUser.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("not found user"));

        KwUserGroup group = kwGroupService.getKwUserGroup(groupList);
        kwUser.setKwUserGroup(group);

        if (!roleList.isEmpty()) {
            List<KwUserRole> modifyRole = roleList.stream().filter(s -> {
                List<KwUserRole> userRoleList = dbKwUser.getRole();
                Optional<KwUserRole> userRole = userRoleList.stream().filter(role -> StringUtils.equalsIgnoreCase(s, role.getRolename().getRolename())).findFirst();
                return !userRole.isPresent();
            }).map(s -> {
                KwUserRoleRule roleRule = kwUserRoleRuleRepository.findByRulename(s);
                KwUserRoleId kwUserRoleId = new KwUserRoleId();
                kwUserRoleId.setRolename(s);
                kwUserRoleId.setUsername(kwUser.getUsername());
                KwUserRole kwUserRole = new KwUserRole();
                kwUserRole.setRolename(kwUserRoleId);
                kwUserRole.setRule(roleRule);
                kwUserRole.setKwUser(kwUser);
                return kwUserRole;
            }).collect(Collectors.toList());

            kwUser.setRole(modifyRole);
            kwUser.getRole().addAll(dbKwUser.getRole());
        }

        // kwUserRole table : delete kwUser
        // ex. DELETE FROM KW_USER_ROLE WHERE USERNAME = :USERNAME

        return kwUserService.modifyUser(kwUser);
    }

    @ResponseBody
    @RequestMapping(value="/security/users/delete", method= RequestMethod.POST)
    public KwUser deleteUser(@ModelAttribute("user") KwUser kwUser) {
        return kwUserService.deleteUser(kwUser);
    }

    @ResponseBody
    @RequestMapping(value="/security/users/save", method= RequestMethod.POST)
    public KwUser saveUser(@ModelAttribute("user") KwUser kwUser, @RequestParam("groupList") String groupList,
                           @RequestParam("roleList") List<String> roleList) {

        KwUserGroup group = kwGroupService.getKwUserGroup(groupList);
        kwUser.setKwUserGroup(group);

        List<KwUserRole> kwUserRoleList = new ArrayList<>();

        for (int i=0; i < roleList.size(); i++) {
            KwUserRoleRule roleRule = kwUserRoleRuleRepository.findByRulename(roleList.get(i));
            KwUserRoleId kwUserRoleId = new KwUserRoleId();
            kwUserRoleId.setRolename(roleList.get(i));
            kwUserRoleId.setUsername(kwUser.getUsername());
            KwUserRole kwUserRole = new KwUserRole();
            kwUserRole.setRolename(kwUserRoleId);
            kwUserRole.setRule(roleRule);
            kwUserRole.setKwUser(kwUser);
            kwUserRoleList.add(kwUserRole);
        }

        kwUser.setRole(kwUserRoleList);
        KwUser saveUser = kwUserService.saveUser(kwUser);

        return saveUser;
    }

    @ResponseBody
    @RequestMapping("/security/roles/user-role-management/save")
    public KwUserRoleRule saveRole(String rolename, String description, String rule) {

        KwUserRoleRule kwUserRoleRule = new KwUserRoleRule();
        kwUserRoleRule.setRulename(rolename);
        kwUserRoleRule.setDescription(description);
        kwUserRoleRule.setRule(rule);
        return kwRoleService.saveKwUserRoleRule(kwUserRoleRule);
    }

    @ResponseBody
    @RequestMapping("/security/roles/user-role-management/modify")
    public void modifyRule(String[] rolenames, String[] rules) {

        for (int i = 0; i < rolenames.length; i++) {
            KwUserRoleRule kwUserRoleRule = kwUserRoleRuleRepository.findByRulename(rolenames[i]);
            for (int j = i; j < rules.length; j++) {
                kwUserRoleRule.setRule(rules[j]);
                kwRoleService.modifyKwUserRoleRule(kwUserRoleRule);
                break;
            }
        }
    }
}
