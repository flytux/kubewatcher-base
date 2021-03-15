package com.kubeworks.watcher.user.service;
import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.*;

import java.util.List;
public interface KwRoleService {

    List<String> getKwUserRoleList();

    List<Page> getKwUserRoleScreenList();

    List<KwUserRoleRule> getKwUserRoleRuleList();

    List<String> getKwUserRoleRule();

    ApiResponse<String> modifyKwUserRoleRule(List<String> rolenameList, List<String> ruleList);

    ApiResponse<String> saveKwUserRoleRule(KwUserRoleRule kwUserRoleRule);
}
