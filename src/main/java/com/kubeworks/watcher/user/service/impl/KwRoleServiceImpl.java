package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUserRoleRule;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.repository.KwUserRoleRepository;
import com.kubeworks.watcher.data.repository.KwUserRoleRuleRepository;
import com.kubeworks.watcher.data.repository.PageRepository;
import com.kubeworks.watcher.user.service.KwRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class KwRoleServiceImpl implements KwRoleService {

    private final KwUserRoleRepository kwUserRoleRepository;
    private final PageRepository pageRepository;
    private final KwUserRoleRuleRepository kwUserRoleRuleRepository;

    @Autowired
    public KwRoleServiceImpl(KwUserRoleRepository kwUserRoleRepository, PageRepository pageRepository, KwUserRoleRuleRepository kwUserRoleRuleRepository) {
        this.kwUserRoleRepository = kwUserRoleRepository;
        this.pageRepository = pageRepository;
        this.kwUserRoleRuleRepository = kwUserRoleRuleRepository;
    }

    /*
        KwUserRole 목록을 조회한다.
    */
    @Override
    public List<String> getKwUserRoleList() {
        return kwUserRoleRepository.findDistinctAllBy();
    }

    /*
        KwUserRole & Rule (screen) 목록을 조회한다.
    */
    @Override
    public List<Page> getKwUserRoleScreenList() {
        return pageRepository.findAllBy(Sort.by(Sort.Direction.ASC, "pageId"));
    }

    /*
        KwUserRole & Rule (rule) 목록을 조회한다.
    */
    @Override
    public List<KwUserRoleRule> getKwUserRoleRuleList() {
        return kwUserRoleRuleRepository.findAllBy();
    }

    @Override
    public List<String> getKwUserRoleRule() {
        return kwUserRoleRuleRepository.findByName();
    }

    @Override
    public ApiResponse<String> modifyKwUserRoleRule(List<String> rolenameList, List<String> ruleList) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            for (int i = 0; i < rolenameList.size(); i++) {
                KwUserRoleRule kwUserRoleRule = kwUserRoleRuleRepository.findByRulename(rolenameList.get(i));
                for (int j = i; j < ruleList.size(); j++) {
                    kwUserRoleRule.setRule(ruleList.get(j));
                    kwUserRoleRule.setRule(kwUserRoleRule.getRule());
                    kwUserRoleRuleRepository.save(kwUserRoleRule);
                    break;
                }
            }
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("role 수정 실패");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Override
    public ApiResponse<String> saveKwUserRoleRule(KwUserRoleRule kwUserRoleRule) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            KwUserRoleRule dbKwRoleRuleOptional = kwUserRoleRuleRepository.findByRulename(kwUserRoleRule.getRulename());
            if (dbKwRoleRuleOptional != null) {
                throw new IllegalArgumentException("이미 등록되어 있는 Role Name 입니다. Role Name=" + kwUserRoleRule.getRulename());
            }
            kwUserRoleRuleRepository.save(kwUserRoleRule);
            response.setSuccess(true);
        } catch (Exception e) {
            log.error("role 등록 실패 // Role Name={}", kwUserRoleRule.getRulename());
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
