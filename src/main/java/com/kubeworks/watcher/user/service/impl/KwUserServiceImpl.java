package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.data.entity.KwUser;
import com.kubeworks.watcher.data.entity.KwUserGroup;
import com.kubeworks.watcher.data.entity.KwUserRoleRule;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.repository.*;
import com.kubeworks.watcher.user.service.KwUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class KwUserServiceImpl implements KwUserService {
    private final KwUserRepository kwUserRepository;
    private final KwUserGroupRepository kwUserGroupRepository;
    private final KwUserRoleRepository kwUserRoleRepository;
    private final PageRepository pageRepository;
    private final KwUserRoleRuleRepository kwUserRoleRuleRepository;

    @Autowired
    public KwUserServiceImpl(KwUserRepository kwUserRepository, KwUserGroupRepository kwUserGroupRepository,
                             KwUserRoleRepository kwUserRoleRepository, PageRepository pageRepository, KwUserRoleRuleRepository kwUserRoleRuleRepository) {
        this.kwUserRepository = kwUserRepository;
        this.kwUserGroupRepository = kwUserGroupRepository;
        this.kwUserRoleRepository = kwUserRoleRepository;
        this.pageRepository = pageRepository;
        this.kwUserRoleRuleRepository = kwUserRoleRuleRepository;
    }

    /*
         KwUser를 ID로 조회한다.
    */
    public KwUser getKwUser(String username) {
        Optional<KwUser> kwUser = kwUserRepository.findById(username);
        return kwUser.get();
    }

    /*
         KwUser 목록을 조회한다.
    */
    public List<KwUser> getKwUserList() {
        return kwUserRepository.findAllBy();
    }

    /*
         KwUserGroup을 그룹명으로 조회한다.
    */
    public KwUserGroup getKwUserGroup(String groupname) {
        Optional<KwUserGroup> kwUserGroup = kwUserGroupRepository.findById(groupname);
        return kwUserGroup.get();
    }

    /*
         KwUser 그룹 목록을 조회한다.
    */
    public List<KwUserGroup> getKwUserGroupList() {
        return kwUserGroupRepository.findAllBy();
    }

    /*
        KwUserRole 목록을 조회한다.
    */
    public List<String> getKwUserRoleList() {
        return kwUserRoleRepository.findDistinctAllBy();
    }

    /*
        KwUserRole & Rule (screen) 목록을 조회한다.
    */
    public List<Page> getKwUserRoleScreenList() {  
        return pageRepository.findAllBy(Sort.by(Sort.Direction.ASC, "pageId"));
    }

    /*
        KwUserRole & Rule (rule) 목록을 조회한다.
    */
    public List<KwUserRoleRule> getKwUserRoleRuleList() {
        return kwUserRoleRuleRepository.findAllBy();
    }
}

