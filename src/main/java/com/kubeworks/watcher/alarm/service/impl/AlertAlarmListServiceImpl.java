package com.kubeworks.watcher.alarm.service.impl;

import com.google.common.collect.Lists;
import com.kubeworks.watcher.alarm.service.AlertAlarmListService;
import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.converter.AlertCategoryConverter;
import com.kubeworks.watcher.data.converter.AlertResourceConverter;
import com.kubeworks.watcher.data.converter.AlertSeverityConverter;
import com.kubeworks.watcher.data.entity.AlertHistory;
import com.kubeworks.watcher.data.repository.AlertHistoryRepository;
import com.kubeworks.watcher.data.vo.AlertListPaginator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class AlertAlarmListServiceImpl implements AlertAlarmListService {

    private static final int PAGE_POST_COUNT = 100;
    private static final int BLOCK_PAGE_NUMBER_COUNT = 5;

    private static final String UPDATE_TIME_STR = "updateTime";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.US);

    private final AlertHistoryRepository alertHistoryRepository;

    @Autowired
    public AlertAlarmListServiceImpl(AlertHistoryRepository alertHistoryRepository) {
        this.alertHistoryRepository = alertHistoryRepository;
    }

    @Override
    public Map<String, Object> alertPageHistory(Integer pageNumber, String startDate, String endDate, String severity, String category, String resource, String target, int pagePostCount) {

        Specification<AlertHistory> spec = Specification.where(searchHistory(startDate, endDate, severity, category, resource, target));
        Pageable pageable = PageRequest.of(pageNumber-1, pagePostCount > 0 ? pagePostCount : 100, Sort.by(Sort.Direction.DESC, UPDATE_TIME_STR));

        Page<AlertHistory> page =  alertHistoryRepository.findAll(spec, pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("histories", page.getContent());
        map.put("totalCount", page.getTotalElements());

        return map;
    }

    @Override
    public Map<String, Object> getPageList(Integer curPageNum, Long totalCount) {

        AlertListPaginator alertListPaginator = new AlertListPaginator(BLOCK_PAGE_NUMBER_COUNT, PAGE_POST_COUNT, totalCount);
        return alertListPaginator.getElasticBlock(curPageNum);
    }

    @Override
    public Map<String, Object> alertSearchHistory(String startDate, String endDate, String severity, String category, String resource, String target) {

        Specification<AlertHistory> spec = Specification.where(searchHistory(startDate, endDate, severity, category, resource, target));
        Pageable pageable = PageRequest.of(0, PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, UPDATE_TIME_STR));

        Page<AlertHistory> page =  alertHistoryRepository.findAll(spec, pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("histories", page.getContent());
        map.put("totalCount", page.getTotalElements());

        return map;
    }

    @Override
    public ApiResponse<String> alertCheck(Long historyId) {
        ApiResponse<String> response = new ApiResponse<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            List<AlertHistory> list = alertHistoryRepository.findAllById(Collections.singleton(historyId));
            AlertHistory alertHistory = list.get(0);
            alertHistory.setResolved(true);
            alertHistory.setUpdateTime(now);
            alertHistoryRepository.save(alertHistory);
            response.setSuccess(true);
        } catch (Exception e){
            log.error("alarm 확인 실패");
            response.setSuccess(false);
            response.setMessage(e.getMessage());
        }

        return response;
    }

    private Specification<AlertHistory> searchHistory(String startDate, String endDate, String severity, String category, String resource, String target){

        return (root, criteriaQuery, criteriaBuilder) -> {
            Map<String, Object> map = new HashMap<>();
            if(startDate != null && !"".equals(startDate)){
                map.put("createTime", startDate);
                map.put(UPDATE_TIME_STR, endDate);
            }
            if(severity != null && !"".equals(severity)) {
                map.put("severity", new AlertSeverityConverter().convertToEntityAttribute(severity));
            }
            if(category != null && !"".equals(category)) {
                map.put("category", new AlertCategoryConverter().convertToEntityAttribute(category));
            }
            if(resource != null && !"".equals(resource)) {
                map.put("resource", new AlertResourceConverter().convertToEntityAttribute(resource));
            }

            map.put("target", target);

            List<Predicate> predicates = createPredicateFromKeyword(map, root, criteriaBuilder);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<Predicate> createPredicateFromKeyword(final Map<String, Object> words, final Root<AlertHistory> root, final CriteriaBuilder builder) {

        final List<Predicate> predicates = Lists.newArrayList();

        words.forEach((k, v) -> {
            if ("target".equals(k) && !StringUtils.isEmpty(v)) {
                predicates.add(builder.like(root.get(k), "%" + v + "%"));
            }
            if ("severity".equals(k)) {
                predicates.add(builder.equal(root.get(k), v));
            }
            if ("category".equals(k) || "resource".equals(k)) {
                predicates.add(builder.equal(root.get("alertRuleId").get(k), v));
            }
            if (UPDATE_TIME_STR.equals(k) && Objects.nonNull(v)) {
                predicates.add(builder.between(root.get(k), createLocalDateTime(words.get("createTime")), createLocalDateTime(v)));
            }
        });

        predicates.add(builder.equal(root.get("resolved"), 0));

        return predicates;
    }

    private LocalDateTime createLocalDateTime(final Object source) {
        return LocalDateTime.parse(String.valueOf(source), FORMATTER);
    }
}
