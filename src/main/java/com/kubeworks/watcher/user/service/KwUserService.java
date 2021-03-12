package com.kubeworks.watcher.user.service;

import com.kubeworks.watcher.data.entity.*;

import java.util.List;

public interface KwUserService {
    KwUser getKwUser(String username);
    List<KwUser> getKwUserList();
    KwUserGroup getKwUserGroup(String groupname);
    List<KwUserGroup> getKwUserGroupList();
    List<String> getKwUserRoleList();
    List<Page> getKwUserRoleScreenList();
    List<KwUserRoleRule> getKwUserRoleRuleList();
}