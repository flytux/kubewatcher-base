package com.kubeworks.watcher.user.service;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUserGroup;

import java.util.List;
public interface KwGroupService {

    KwUserGroup getKwUserGroup(String groupname);

    ApiResponse<String> saveGroup(KwUserGroup kwUserGroup);

    ApiResponse<String> deleteGroup(KwUserGroup kwUserGroup);

    List<KwUserGroup> getKwUserGroupList();

}
