package com.kubeworks.watcher.user.service.impl;

import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.KwUserRoleRule;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.repository.KwUserRoleRuleRepository;
import com.kubeworks.watcher.data.repository.PageRepository;
import com.kubeworks.watcher.user.service.KwRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
@Service
public class KwRoleServiceImpl implements KwRoleService {

    private final PageRepository pr;
    private final KwUserRoleRuleRepository kurrr;

    @Autowired
    public KwRoleServiceImpl(final PageRepository pr, final KwUserRoleRuleRepository kurrr) {
        this.pr = pr;
        this.kurrr = kurrr;
    }

    @Override
    public List<Page> searchKwUserRoleScreenList() {
        return pr.findAllBy(Sort.by(Sort.Direction.ASC, "pageId"));
    }

    @Override
    public List<KwUserRoleRule> searchKwUserRoleRuleList() {
        return kurrr.findAllBy();
    }

    @Override
    public List<String> searchKwUserRoleRule() {
        return kurrr.findByName();
    }

    @Override
    public ApiResponse<String> createKwUserRoleRule(final KwUserRoleRule rule) {

        try {
            if (Objects.nonNull(kurrr.findByRulename(rule.getRulename()))) {
                throw new IllegalArgumentException("이미 등록되어 있는 Role Name 입니다. Role Name=" + rule.getRulename());
            }
            kurrr.save(rule);

            return createSuccessResponse();
        } catch (final Exception e) {
            log.warn("role 등록 실패 // Role Name={}", rule.getRulename());
            return createResponse(false, e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> updateKwUserRoleRule(final List<String> roles, final List<String> rules) {

        if (CollectionUtils.isEmpty(roles) || CollectionUtils.isEmpty(rules) || roles.size() != rules.size()) {
            log.info("roles and rules data not matched");
            return createResponse(false, "roles and rules data not matched");
        }

        try {
            IntStream.range(0, roles.size()).forEach(e -> {
                final KwUserRoleRule rule = kurrr.findByRulename(roles.get(e));
                rule.setRule(rules.get(e)); kurrr.save(rule);
            });

            return createSuccessResponse();
        } catch (final Exception e) {
            log.warn("role 수정 실패");
            return createResponse(false, e.getMessage());
        }
    }

    private ApiResponse<String> createSuccessResponse() {
        return createResponse(true, null);
    }

    private ApiResponse<String> createResponse(final boolean value, @Nullable final String message) {
        final ApiResponse<String> res = new ApiResponse<>();
        res.setSuccess(value); res.setMessage(message);
        return res;
    }
}
