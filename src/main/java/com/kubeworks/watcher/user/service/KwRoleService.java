package com.kubeworks.watcher.user.service;
import com.kubeworks.watcher.data.entity.*;

import java.util.List;
public interface KwRoleService {

    //KwUserRole deleteRole(KwUserRole kwuserRole);

    List<KwUserRole> getUserRole();

    KwUserRole saveKwUserRole(KwUserRole kwUserRole);

    List<String> getKwUserRoleList();

    List<Page> getKwUserRoleScreenList();

    List<KwUserRoleRule> getKwUserRoleRuleList();

    List<String> getKwUserRoleRule();

    KwUserRoleRule modifyKwUserRoleRule(KwUserRoleRule kwUserRoleRule);

    KwUserRoleRule saveKwUserRoleRule(KwUserRoleRule kwUserRoleRule);
}
