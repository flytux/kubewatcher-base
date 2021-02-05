package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.data.entity.KwUserRole;
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
import java.util.Optional;

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
    public List<String> getKwUserRoleList() {
        return kwUserRoleRepository.findDistinctAllBy();
    }

    // TODO
    public List<KwUserRole> getUserRole() {
        return kwUserRoleRepository.findAllBy();
    }

    // add kwUserRole
    public KwUserRole saveKwUserRole(KwUserRole kwUserRole) {
        return kwUserRoleRepository.save(kwUserRole);
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

    // select
    public List<String> getKwUserRoleRule() {
        return kwUserRoleRuleRepository.findByName();
    }

    // modify
    public KwUserRoleRule modifyKwUserRoleRule(KwUserRoleRule kwUserRoleRule) {
        kwUserRoleRule.setRule(kwUserRoleRule.getRule());
        return kwUserRoleRuleRepository.save(kwUserRoleRule);
    }

    // insert
    public KwUserRoleRule saveKwUserRoleRule(KwUserRoleRule kwUserRoleRule) {
        return kwUserRoleRuleRepository.save(kwUserRoleRule);
    }
}
