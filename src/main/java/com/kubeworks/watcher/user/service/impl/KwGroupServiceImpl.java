package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.data.entity.KwUserGroup;
import com.kubeworks.watcher.data.entity.KwUserRole;
import com.kubeworks.watcher.data.repository.*;
import com.kubeworks.watcher.user.service.KwGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class KwGroupServiceImpl implements KwGroupService {

    private final KwUserRepository kwUserRepository;
    private final KwUserGroupRepository kwUserGroupRepository;
    private final KwUserRoleRepository kwUserRoleRepository;
    private final PageRepository pageRepository;
    private final KwUserRoleRuleRepository kwUserRoleRuleRepository;

    @Autowired
    public KwGroupServiceImpl(KwUserRepository kwUserRepository, KwUserGroupRepository kwUserGroupRepository,
                             KwUserRoleRepository kwUserRoleRepository, PageRepository pageRepository, KwUserRoleRuleRepository kwUserRoleRuleRepository) {
        this.kwUserRepository = kwUserRepository;
        this.kwUserGroupRepository = kwUserGroupRepository;
        this.kwUserRoleRepository = kwUserRoleRepository;
        this.pageRepository = pageRepository;
        this.kwUserRoleRuleRepository = kwUserRoleRuleRepository;
    }

    /*
      KwUserGroup을 그룹명으로 조회한다.
 */
    public KwUserGroup getKwUserGroup(String groupname) {
        Optional<KwUserGroup> kwUserGroup = kwUserGroupRepository.findById(groupname);
        return kwUserGroup.get();
    }


    // kwUserGroup save(insert)
    public KwUserGroup saveGroup(KwUserGroup kwUserGroup) {
        // TODO null, type check
//        KwUserGroup saveGroup = kwUserGroupRepository.save(kwUserGroup);
        return kwUserGroupRepository.save(kwUserGroup);
    }


    // kwUserGroup delete
    public KwUserGroup deleteGroup(KwUserGroup kwUserGroup) {
        Optional<KwUserGroup> group = kwUserGroupRepository.findById(kwUserGroup.getGroupname());
        log.info(">>>>> groupName : " + kwUserGroup.getGroupname());
        group.ifPresent(groupName -> {
            kwUserGroupRepository.delete(groupName);
        });

        return kwUserGroup;
    }

    // kwUserGroup delete
    public KwUserRole deleteRole(KwUserRole kwUserRole) {
        kwUserRoleRepository.delete(kwUserRole);
        return kwUserRole;
    }

    /*
         KwUser 그룹 목록을 조회한다.
    */
    public List<KwUserGroup> getKwUserGroupList() {
        return kwUserGroupRepository.findAllBy();
    }
}
