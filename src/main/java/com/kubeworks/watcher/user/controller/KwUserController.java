package com.kubeworks.watcher.user.controller;

import com.kubeworks.watcher.data.entity.*;
import com.kubeworks.watcher.preference.service.PageViewService;
import com.kubeworks.watcher.user.service.KwGroupService;
import com.kubeworks.watcher.user.service.KwRoleService;
import com.kubeworks.watcher.user.service.KwUserService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class KwUserController {

    private final KwUserService kwUserService;
    private final KwGroupService kwGroupService;
    private final KwRoleService kwRoleService;
    private final PageViewService pageViewService;

    @ModelAttribute("user")
    public KwUser getUser() {
        return new KwUser();
    }

    @ModelAttribute("role")
    public KwUserRole getRole() {
        return new KwUserRole();
    }

    @GetMapping(value = "/security/groups")
    public String groupList(Model model) {
        List<KwUserGroup> groupList = kwGroupService.getKwUserGroupList();
        model.addAttribute("groupList", groupList);

        Page page = pageViewService.getPageView(400);
        model.addAttribute("page", page);

        return "security/groups";
    }

    @GetMapping(value = "/security/groups/{groupname}", produces = MediaType.TEXT_HTML_VALUE)
    public String group(Model model, @PathVariable String groupname) {
        KwUserGroup group = kwGroupService.getKwUserGroup(groupname);
        model.addAttribute("group", group);

        return "security/groups :: modalContents";
    }
    @GetMapping(value = "/security/users")
    public String userList(Model model) {
        List<KwUser> userList = kwUserService.getKwUserList();
        model.addAttribute("userList", userList);

        Page page = pageViewService.getPageView(410);
        model.addAttribute("page", page);

        List<KwUserGroup> groups = kwGroupService.getKwUserGroupList();
        model.addAttribute("groupList", groups);

        List<String> roles = kwRoleService.getKwUserRoleRule();
        model.addAttribute("roleList", roles);

        return "security/users";
    }

    @GetMapping(value = "/security/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String user (Model model, KwUser user, @PathVariable String username) {
        user = kwUserService.getKwUser(username);
        model.addAttribute("user", user);
        if (CollectionUtils.isNotEmpty(user.getRole())) {
            model.addAttribute("userRoles", user.getRole().stream().map(kwUserRole -> kwUserRole.getRolename().getRolename()).collect(Collectors.toList()));
        } else {
            model.addAttribute("userRoles", "");
        }

        List<KwUserGroup> groups = kwGroupService.getKwUserGroupList();
        model.addAttribute("groups", groups);

        List<String> roles = kwRoleService.getKwUserRoleRule();

        model.addAttribute("roles", roles);

        return "security/users :: modalContents";
    }

    @GetMapping(value = "/security/roles/user-role-management")
    public String roleRuleList(Model model) {
        List<Page> pageList = kwRoleService.getKwUserRoleScreenList();
        List<KwUserRoleRule> ruleList = kwRoleService.getKwUserRoleRuleList();

        //map (pageId, rule bit) -> list -> model
        List<Map> list = new ArrayList();
        Map<Long, String> pageMap = pageList.stream().collect(Collectors.toMap(x->x.getPageId(),x->x.getTitle()));

        list.add(pageMap);
        String [] bit = new String[pageList.size()];
        for (KwUserRoleRule b : ruleList) {
            bit = b.getRule().split("");
            Map<Long, String> ruleSet = new TreeMap<>();
            for (int count = 0; count < pageList.size(); count++) {
                ruleSet.put(pageList.get(count).getPageId(), bit[count]);
            }
            list.add(ruleSet);
        }
        model.addAttribute("pageRuleList", list);
        model.addAttribute("ruleList", ruleList);

        Page page = pageViewService.getPageView(420);
        model.addAttribute("page", page);

        return "security/roles/user-role-management";
    }
}

