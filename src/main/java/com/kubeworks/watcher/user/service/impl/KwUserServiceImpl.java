package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.base.ApiResponse;
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
import java.time.LocalDateTime;
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

    @Transactional
    @Override
    public ApiResponse<String> modifyUser(KwUser kwUser, String groupName, List<String> roleList) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            Optional<KwUser> dbUserOptional = kwUserRepository.findById(kwUser.getUsername());
            KwUser dbUser = dbUserOptional.orElseThrow(() -> new IllegalArgumentException("user not found // username=" + kwUser.getUsername()));
            dbUser.setPassword(kwUser.getPassword());
            dbUser.setDept(kwUser.getDept());

            KwUserGroup group = kwGroupService.getKwUserGroup(groupName);
            dbUser.setKwUserGroup(group);

            if (CollectionUtils.isEmpty(roleList)) {
                dbUser.setRole(Collections.emptyList(), "modify");
            } else {
                LocalDateTime now = LocalDateTime.now();
                List<KwUserRole> userRoles = roleList.stream().map(s -> {
                    KwUserRoleRule roleRule = kwUserRoleRuleRepository.findByRulename(s);
                    KwUserRoleId kwUserRoleId = new KwUserRoleId();
                    kwUserRoleId.setRolename(s);
                    kwUserRoleId.setUsername(dbUser.getUsername());
                    KwUserRole kwUserRole = new KwUserRole();
                    kwUserRole.setRolename(kwUserRoleId);
                    kwUserRole.setRule(roleRule);
                    kwUserRole.setKwUser(dbUser);
                    kwUserRole.setCreateTime(now);
                    kwUserRole.setUpdateTime(now);
                    return kwUserRole;
                }).collect(Collectors.toList());
                dbUser.setRole(userRoles, "modify");
            }
            kwUserRepository.save(dbUser);
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("사용자 수정 실패 // username={}", kwUser.getUsername());
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Transactional
    @Override
    public ApiResponse<String> saveUser(KwUser kwUser, String groupName, List<String> roleList) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            KwUserGroup group = kwGroupService.getKwUserGroup(groupName);
            kwUser.setKwUserGroup(group);

            LocalDateTime now = LocalDateTime.now();
            kwUser.setCreateTime(now);
            kwUser.setUpdateTime(now);

            if (CollectionUtils.isEmpty(roleList)) {
                kwUser.setRole(Collections.emptyList(),"save");
            } else {

                List<KwUserRole> userRoles = roleList.stream().map(s -> {
                    KwUserRoleRule roleRule = kwUserRoleRuleRepository.findByRulename(s);
                    KwUserRoleId kwUserRoleId = new KwUserRoleId();
                    kwUserRoleId.setRolename(s);
                    kwUserRoleId.setUsername(kwUser.getUsername());
                    KwUserRole kwUserRole = new KwUserRole();
                    kwUserRole.setRolename(kwUserRoleId);
                    kwUserRole.setRule(roleRule);
                    kwUserRole.setKwUser(kwUser);
                    kwUserRole.setCreateTime(now);
                    kwUserRole.setUpdateTime(now);
                    return kwUserRole;
                }).collect(Collectors.toList());
                kwUser.setRole(userRoles, "save");
            }
            kwUserRepository.save(kwUser);
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("사용자 등록 실패 // username={}", kwUser.getUsername());
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Transactional
    @Override
    public ApiResponse<String> deleteUser(KwUser kwUser) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            Optional<KwUser> dbKwUserOptional = kwUserRepository.findById(kwUser.getUsername());
            KwUser dbKwUser = dbKwUserOptional.orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 입니다."));
            kwUserRepository.delete(dbKwUser);
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("사용자 삭제 실패 // username={}", kwUser.getUsername());
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

}

