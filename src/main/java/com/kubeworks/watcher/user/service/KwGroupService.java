package com.kubeworks.watcher.user.service;
import com.kubeworks.watcher.data.entity.*;

import java.util.List;
public interface KwGroupService {

    KwUserGroup getKwUserGroup(String groupname);

    KwUserGroup saveGroup(KwUserGroup kwUserGroup);

    KwUserGroup deleteGroup(KwUserGroup kwUserGroup);

    List<KwUserGroup> getKwUserGroupList();

}
