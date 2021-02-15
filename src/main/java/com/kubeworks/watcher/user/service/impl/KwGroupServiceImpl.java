package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUserGroup;
import com.kubeworks.watcher.data.repository.*;
import com.kubeworks.watcher.user.service.KwGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class KwGroupServiceImpl implements KwGroupService {
    private final KwUserGroupRepository kwUserGroupRepository;

    @Autowired
    public KwGroupServiceImpl(KwUserGroupRepository kwUserGroupRepository) {
        this.kwUserGroupRepository = kwUserGroupRepository;
    }

    /*
      KwUserGroup을 그룹명으로 조회한다.
    */
    @Override
    public KwUserGroup getKwUserGroup(String groupname) {
        Optional<KwUserGroup> kwUserGroup = kwUserGroupRepository.findById(groupname);
        return kwUserGroup.get();
    }

    /*
         KwUser 그룹 목록을 조회한다.
    */
    @Override
    public List<KwUserGroup> getKwUserGroupList() {
        return kwUserGroupRepository.findAllBy();
    }

    @Override
    public ApiResponse<String> saveGroup(KwUserGroup kwUserGroup) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            kwUserGroupRepository.save(kwUserGroup);
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("그룹 등록 실패 // groupname={}", kwUserGroup.getGroupname());
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Transactional
    @Override
    public ApiResponse<String> deleteGroup(KwUserGroup kwUserGroup) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            Optional<KwUserGroup> dbKwUserGroupOptional = kwUserGroupRepository.findById(kwUserGroup.getGroupname());
            KwUserGroup dbKwUserGroup = dbKwUserGroupOptional.orElseThrow(() -> new IllegalArgumentException("잘못된 그룹명 입니다."));
            kwUserGroupRepository.delete(dbKwUserGroup);
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("그룹 삭제 실패 // groupname={}", kwUserGroup.getGroupname());
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
