package com.kubeworks.watcher.user.controller;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUser;
import com.kubeworks.watcher.data.entity.KwUserGroup;
import com.kubeworks.watcher.data.entity.KwUserRoleRule;
import com.kubeworks.watcher.user.service.KwGroupService;
import com.kubeworks.watcher.user.service.KwRoleService;
import com.kubeworks.watcher.user.service.KwUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.*;

@Controller
@AllArgsConstructor(onConstructor_={@Autowired})
public class KwUserRestController {

    private static final String USER_LIST_STR = "userList";
    private static final String GROUP_LIST_STR = "groupList";
    private static final String VIEW_NAME = "security/users";

    private final KwUserService kwUserService;
    private final KwGroupService kwGroupService;
    private final KwRoleService kwRoleService;

    private final SpringTemplateEngine springTemplateEngine;

    @ResponseBody
    @PostMapping("/security/groups/save")
    public Map<String, Object> saveGroup(@ModelAttribute KwUserGroup kwUserGroup) {
        Map<String, Object> saveGroup = new HashMap<>();

        ApiResponse<String> save = kwGroupService.saveGroup(kwUserGroup);
        List<KwUserGroup> groupList = kwGroupService.getKwUserGroupList();

        saveGroup.put(GROUP_LIST_STR, groupList);
        String groupHtml = springTemplateEngine.process("security/groups",
            Collections.singleton(GROUP_LIST_STR), new Context(Locale.KOREA, saveGroup));

        saveGroup.put("html", groupHtml);
        saveGroup.put("save", save);

        return saveGroup;
    }

    @ResponseBody
    @PostMapping("/security/groups/delete")
    public Map<String, Object> deleteGroup(@ModelAttribute KwUserGroup kwUserGroup) {

        Map<String, Object> deleteGroup = new HashMap<>();

        ApiResponse<String> delete = kwGroupService.deleteGroup(kwUserGroup);
        List<KwUserGroup> groupList = kwGroupService.getKwUserGroupList();

        deleteGroup.put(GROUP_LIST_STR, groupList);
        String groupHtml = springTemplateEngine.process("security/groups",
            Collections.singleton(GROUP_LIST_STR), new Context(Locale.KOREA, deleteGroup));

        deleteGroup.put("html", groupHtml);
        deleteGroup.put("delete", delete);

        return deleteGroup;
    }

    @ResponseBody
    @PostMapping("/security/users/modify")
    public Map<String, Object> modifyUser(@ModelAttribute("user") KwUser kwUser, @RequestParam("groupList") String groupName,
                             @RequestParam("roleList") List<String> roleList) {

        Map<String, Object> modifyUser = new HashMap<>();

        ApiResponse<String> modify = kwUserService.modifyUser(kwUser, groupName, roleList);
        List<KwUser> userList = kwUserService.getKwUserList();

        modifyUser.put(USER_LIST_STR, userList);
        String userHtml = springTemplateEngine.process(VIEW_NAME,
            Collections.singleton(USER_LIST_STR), new Context(Locale.KOREA, modifyUser));

        modifyUser.put("html", userHtml);
        modifyUser.put("modify", modify);

        return modifyUser;
    }

    @ResponseBody
    @PostMapping("/security/users/delete")
    public Map<String, Object> deleteUser(@ModelAttribute("user") KwUser kwUser) {

        Map<String, Object> deleteUser = new HashMap<>();

        ApiResponse<String> delete = kwUserService.deleteUser(kwUser);
        List<KwUser> userList = kwUserService.getKwUserList();

        deleteUser.put(USER_LIST_STR, userList);
        String userHtml = springTemplateEngine.process(VIEW_NAME,
            Collections.singleton(USER_LIST_STR), new Context(Locale.KOREA, deleteUser));

        deleteUser.put("html", userHtml);
        deleteUser.put("delete", delete);

        return deleteUser;
    }

    @ResponseBody
    @PostMapping("/security/users/save")
    public Map<String, Object> saveUser(@ModelAttribute("user") KwUser kwUser, @RequestParam("groupList") String groupName,
                           @RequestParam("roleList") List<String> roleList) {

        Map<String, Object> saveUser = new HashMap<>();

        ApiResponse<String> save = kwUserService.saveUser(kwUser, groupName, roleList);
        List<KwUser> userList = kwUserService.getKwUserList();

        saveUser.put(USER_LIST_STR, userList);
        String userHtml = springTemplateEngine.process(VIEW_NAME,
            Collections.singleton(USER_LIST_STR), new Context(Locale.KOREA, saveUser));

        saveUser.put("html", userHtml);
        saveUser.put("save", save);

        return saveUser;
    }

    @ResponseBody
    @PostMapping("/security/roles/user-role-management/save")
    public ApiResponse<String> saveRole(@ModelAttribute KwUserRoleRule kwUserRoleRule) {
        return kwRoleService.createKwUserRoleRule(kwUserRoleRule); }

    @ResponseBody
    @PostMapping("/security/roles/user-role-management/modify")
    public ApiResponse<String> modifyRule(@RequestParam("rolenameList") List<String> rolenameList, @RequestParam("ruleList") List<String> ruleList) {
        return kwRoleService.updateKwUserRoleRule(rolenameList, ruleList);
    }
}
