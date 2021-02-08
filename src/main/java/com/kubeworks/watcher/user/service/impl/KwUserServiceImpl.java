package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.data.entity.*;
import com.kubeworks.watcher.data.repository.KwUserRepository;
import com.kubeworks.watcher.data.repository.KwUserRoleRuleRepository;
import com.kubeworks.watcher.user.service.KwGroupService;
import com.kubeworks.watcher.user.service.KwUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KwUserServiceImpl implements KwUserService {

    private final KwUserRepository kwUserRepository;
    private final KwUserRoleRuleRepository kwUserRoleRuleRepository;
    private final KwGroupService kwGroupService;

    @Autowired
    public KwUserServiceImpl(KwUserRepository kwUserRepository, KwUserRoleRuleRepository kwUserRoleRuleRepository, KwGroupService kwGroupService) {
        this.kwUserRepository = kwUserRepository;
        this.kwUserRoleRuleRepository = kwUserRoleRuleRepository;
        this.kwGroupService = kwGroupService;
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
//        if (CollectionUtils.isEmpty(kwUser.getRole())) {
//            dbUser.getRole().forEach(kwUserRole -> {
////                kwUserRoleRepository.deleteById(kwUserRole.getRolename());
//                kwUserRoleRepository.deleteById(kwUserRole.getRolename());
//            });
//        }
        dbUser.setRole(kwUser.getRole());
        System.out.println("kwUser = " + kwUser.getUsername());

        kwUserRepository.save(dbUser);
        return dbUser;
    }

    @Transactional
    @Override
    public KwUser modifyUser(KwUser kwUser, String groupName, List<String> roleList) {

        Optional<KwUser> dbUserOptional = kwUserRepository.findById(kwUser.getUsername());
        log.info(">>>>> userList : " + kwUser.getUsername());
        KwUser dbUser = dbUserOptional.orElseThrow(() -> new IllegalArgumentException("user not found // username=" + kwUser.getUsername()));
        dbUser.setPassword(kwUser.getPassword());
        dbUser.setDept(kwUser.getDept());

        KwUserGroup group = kwGroupService.getKwUserGroup(groupName);
        dbUser.setKwUserGroup(group);

        if (CollectionUtils.isEmpty(roleList)) {
            dbUser.setRole(Collections.emptyList());
        } else {
            List<KwUserRole> userRoles = roleList.stream().map(s -> {
                KwUserRoleRule roleRule = kwUserRoleRuleRepository.findByRulename(s);
                KwUserRoleId kwUserRoleId = new KwUserRoleId();
                kwUserRoleId.setRolename(s);
                kwUserRoleId.setUsername(dbUser.getUsername());
                KwUserRole kwUserRole = new KwUserRole();
                kwUserRole.setRolename(kwUserRoleId);
                kwUserRole.setRule(roleRule);
                kwUserRole.setKwUser(dbUser);
                return kwUserRole;
            }).collect(Collectors.toList());
            dbUser.setRole(userRoles);
        }

        return kwUserRepository.save(dbUser);
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

