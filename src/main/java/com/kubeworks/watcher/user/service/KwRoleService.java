package com.kubeworks.watcher.user.service;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUserRoleRule;
import com.kubeworks.watcher.data.entity.Page;

import java.util.List;
public interface KwRoleService {

    List<Page> searchKwUserRoleScreenList();
    List<KwUserRoleRule> searchKwUserRoleRuleList();
    List<String> searchKwUserRoleRule();
    ApiResponse<String> createKwUserRoleRule(final KwUserRoleRule rule);
    ApiResponse<String> updateKwUserRoleRule(final List<String> roles, final List<String> rules);
}
