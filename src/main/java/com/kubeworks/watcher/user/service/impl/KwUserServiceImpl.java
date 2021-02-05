package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.data.entity.KwUser;
import com.kubeworks.watcher.data.repository.*;
import com.kubeworks.watcher.user.service.KwUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    // KwUser modify(update)
    public KwUser modifyUser(KwUser kwUser) {
        Optional<KwUser> dbUserOptional = kwUserRepository.findById(kwUser.getUsername());
        log.info(">>>>> userList : " + kwUser.getUsername());
        KwUser dbUser = dbUserOptional.orElseThrow(() -> new IllegalArgumentException("user not found // username=" + kwUser.getUsername()));
        dbUser.setPassword(kwUser.getPassword());
        dbUser.setDept(kwUser.getDept());
        dbUser.setKwUserGroup(kwUser.getKwUserGroup());

        // TODO
        if (CollectionUtils.isEmpty(kwUser.getRole())) {
            dbUser.getRole().forEach(kwUserRole -> {

                kwUserRoleRepository.deleteById(kwUserRole.getRolename());
            });
        }
        dbUser.setRole(kwUser.getRole());
        System.out.println("kwUser = " + kwUser.getUsername());

        kwUserRepository.save(dbUser);
        return dbUser;
    }

    // KwUser delete
    public KwUser deleteUser(KwUser KwUser) {
        Optional<KwUser> userList = kwUserRepository.findById(KwUser.getUsername());
        log.info(">>>>> user : " + KwUser.getUsername());
        userList.ifPresent(user -> {
            kwUserRepository.delete(user);
        });

        return KwUser;
    }

    // KwUser insert
    public KwUser saveUser(KwUser kwUser) {
        KwUser saveUser = kwUserRepository.save(kwUser);
        log.info(">>>>> group group : " + saveUser);
        return saveUser;
    }

}

