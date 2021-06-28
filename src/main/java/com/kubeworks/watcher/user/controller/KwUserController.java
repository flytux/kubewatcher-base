package com.kubeworks.watcher.user.controller;

import com.google.common.collect.Lists;
import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.data.entity.KwUser;
import com.kubeworks.watcher.data.entity.KwUserRole;
import com.kubeworks.watcher.data.entity.KwUserRoleRule;
import com.kubeworks.watcher.data.entity.Page;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value="/security")
@AllArgsConstructor(onConstructor_={@Autowired})
public class KwUserController implements BaseController {

    private static final long USER_LIST_MENU_ID = 410;
    private static final long USER_ROLE_MENU_ID = 420;
    private static final long USER_GROUP_MENU_ID = 400;

    private static final String VIEW_USERS = "users";
    private static final String VIEW_GROUPS = "groups";

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

    @GetMapping(value="/groups")
    public String groupList(final Model model) {

        model.addAttribute("groupList", kwGroupService.getKwUserGroupList());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(USER_GROUP_MENU_ID));

        return createViewName(VIEW_GROUPS);
    }

    @GetMapping(value="/groups/{groupname}")
    public String group(final Model model, @PathVariable final String groupname) {
        model.addAttribute("group", kwGroupService.getKwUserGroup(groupname));
        return createViewName(VIEW_GROUPS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/users")
    public String userList(final Model model) {

        model.addAttribute("userList", kwUserService.getKwUserList());
        model.addAttribute("groupList", kwGroupService.getKwUserGroupList());
        model.addAttribute("roleList", kwRoleService.getKwUserRoleRule());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(USER_LIST_MENU_ID));

        return createViewName(VIEW_USERS);
    }

    @GetMapping(value="/users/{username}", produces=MediaType.APPLICATION_JSON_VALUE)
    public String user(final Model model, @PathVariable final String username) {

        final KwUser user = kwUserService.getKwUser(username);
        model.addAttribute("user", user);

        if (CollectionUtils.isNotEmpty(user.getRole())) {
            model.addAttribute("userRoles", user.getRole().stream().map(kwUserRole -> kwUserRole.getRolename().getRolename()).collect(Collectors.toList()));
        } else {
            model.addAttribute("userRoles", "");
        }

        model.addAttribute("roles", kwRoleService.getKwUserRoleRule());
        model.addAttribute(VIEW_GROUPS, kwGroupService.getKwUserGroupList());

        return createViewName(VIEW_USERS, Props.MODAL_CONTENTS);
    }

    @GetMapping(value="/roles/user-role-management")
    public String roleRuleList(final Model model) {

        final List<Page> pageList = kwRoleService.getKwUserRoleScreenList();
        final List<KwUserRoleRule> ruleList = kwRoleService.getKwUserRoleRuleList();

        // map (pageId, rule bit) -> list -> model
        final List<Map<Long, String>> list = Lists.newArrayListWithExpectedSize(ruleList.size() + 1);

        list.add(pageList.stream().collect(Collectors.toMap(Page::getPageId, Page::getTitle)));

        for (final KwUserRoleRule rule : ruleList) {
            final String[] bit = rule.getRule().split("");
            final Map<Long, String> ruleSetMap = new TreeMap<>();

            for (int count=0; count<pageList.size(); count++) {
                ruleSetMap.put(pageList.get(count).getPageId(), bit[count]);
            }

            list.add(ruleSetMap);
        }

        model.addAttribute("pageRuleList", list);
        model.addAttribute("ruleList", ruleList);
        model.addAttribute(Props.PAGE, pageViewService.getPageView(USER_ROLE_MENU_ID));

        return createViewName("roles/user-role-management");
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "security/";
    }
}
