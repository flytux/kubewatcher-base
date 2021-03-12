package com.kubeworks.watcher.user.controller;

import com.kubeworks.watcher.data.entity.*;
import com.kubeworks.watcher.preference.service.PageViewService;
import com.kubeworks.watcher.user.service.KwUserService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class KwUserController {

    private final KwUserService kwUserService;
    private final PageViewService pageViewService;

    @GetMapping(value = "/security/users")
    public String userList(Model model) {
        List<KwUser> userList = kwUserService.getKwUserList();
        model.addAttribute("userList", userList);

        Page page = pageViewService.getPageView(410);
        model.addAttribute("page", page);

        return "security/users";
    }

    @GetMapping(value = "/security/users/{username}", produces = MediaType.TEXT_HTML_VALUE)
    public String user(Model model, @PathVariable String username) {
        KwUser user = kwUserService.getKwUser(username);
        model.addAttribute("user", user);

        List<KwUserGroup> groups = kwUserService.getKwUserGroupList();
        model.addAttribute("groups", groups);

        List<String> roles = kwUserService.getKwUserRoleList();

        //유저 상세조회 모달 화면에 전체 Role 보여주고, 해당 사용자에게 할당된 Role 체크박스에 체크
        Map<String, String> roleList = new HashedMap();
        for (String list : roles) {
            if (user.getRole().size() > 0) {
                for (KwUserRole userRole : user.getRole()) {
                    if (list.equals(userRole.getRolename().getRolename())) {
                        roleList.put(list, userRole.getRolename().getRolename());
                        break;
                    } else {
                        roleList.put(list, "");
                    }
                }
            } else {
                roleList.put(list, "");
            }
        }
        model.addAttribute("roles", roleList);

        return "security/users :: modalContents";
    }

    @GetMapping(value = "/security/groups")
    public String groupList(Model model) {
        List<KwUserGroup> groupList = kwUserService.getKwUserGroupList();
        model.addAttribute("groupList", groupList);

        Page page = pageViewService.getPageView(400);
        model.addAttribute("page", page);

        return "security/groups";
    }

    @GetMapping(value = "/security/groups/{groupname}", produces = MediaType.TEXT_HTML_VALUE)
    public String group(Model model, @PathVariable String groupname) {
        KwUserGroup group = kwUserService.getKwUserGroup(groupname);
        model.addAttribute("group", group);

        return "security/groups :: modalContents";
    }

    @GetMapping(value = "/security/roles/user-role-management")
    public String roleRuleList(Model model) {
        List<Page> pageList = kwUserService.getKwUserRoleScreenList();
        List<KwUserRoleRule> ruleList = kwUserService.getKwUserRoleRuleList();

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

