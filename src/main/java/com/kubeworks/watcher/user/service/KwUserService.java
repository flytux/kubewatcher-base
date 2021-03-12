package com.kubeworks.watcher.user.service;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUser;

import java.util.List;

public interface KwUserService {
    KwUser getKwUser(String username);

    List<KwUser> getKwUserList();

    ApiResponse<String> deleteUser(KwUser kwUser);

    ApiResponse<String> saveUser(KwUser kwUser, String groupName, List<String> roleList);

    ApiResponse<String> modifyUser(KwUser kwUser, String groupName, List<String> roleList);
}