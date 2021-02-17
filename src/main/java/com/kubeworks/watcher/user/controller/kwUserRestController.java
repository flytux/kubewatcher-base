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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class kwUserRestController {

    private final KwUserService kwUserService;
    private final KwGroupService kwGroupService;
    private final KwRoleService kwRoleService;

    @ResponseBody
    @PostMapping("/security/groups/save")
    public ApiResponse<String> saveGroup(@ModelAttribute KwUserGroup kwUserGroup) {
        return kwGroupService.saveGroup(kwUserGroup);
    }

    @ResponseBody
    @PostMapping("/security/groups/{groupname}/delete")
    public ApiResponse<String> deleteGroup(@ModelAttribute KwUserGroup kwUserGroup) {
        return kwGroupService.deleteGroup(kwUserGroup);
    }

    @ResponseBody
    @PostMapping("/security/users/modify")
    public ApiResponse<String> modifyUser(@ModelAttribute("user") KwUser kwUser, @RequestParam("groupList") String groupList,
                             @RequestParam("roleList") List<String> roleList, Model model) {
        return kwUserService.modifyUser(kwUser, groupList, roleList);
    }

    @ResponseBody
    @PostMapping("/security/users/delete")
    public ApiResponse<String> deleteUser(@ModelAttribute("user") KwUser kwUser) {
        return kwUserService.deleteUser(kwUser);
    }

    @ResponseBody
    @PostMapping("/security/users/save")
    public ApiResponse<String> saveUser(@ModelAttribute("user") KwUser kwUser, @RequestParam("groupList") String groupList,
                           @RequestParam("roleList") List<String> roleList) {
        return kwUserService.saveUser(kwUser, groupList, roleList);
    }

    @ResponseBody
    @PostMapping("/security/roles/user-role-management/save")
    public ApiResponse<String> saveRole(@ModelAttribute KwUserRoleRule kwUserRoleRule) {
        return kwRoleService.saveKwUserRoleRule(kwUserRoleRule); }

    @ResponseBody
    @PostMapping("/security/roles/user-role-management/modify")
    public ApiResponse<String> modifyRule(@RequestParam("rolenameList") List<String> rolenameList, @RequestParam("ruleList") List<String> ruleList) {
        return kwRoleService.modifyKwUserRoleRule(rolenameList, ruleList);
    }
}
