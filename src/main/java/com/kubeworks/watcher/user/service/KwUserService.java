package com.kubeworks.watcher.user.service;

import com.kubeworks.watcher.data.entity.KwUser;

import java.util.List;

public interface KwUserService {
    KwUser getKwUser(String username);

    List<KwUser> getKwUserList();

    KwUser modifyUser(KwUser kwUser);

    KwUser deleteUser(KwUser kwUser);

    KwUser saveUser(KwUser kwUser);

    KwUser modifyUser(KwUser kwUser, String groupName, List<String> roleList);
}