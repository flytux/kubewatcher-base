package com.kubeworks.watcher.alarm.service.impl;

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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class AlertAlarmListServiceImpl implements AlertAlarmListService {

    private final AlertHistoryRepository alertHistoryRepository;
    private static final int BLOCK_PAGE_NUMBER_COUNT = 5;
    private static final int PAGE_POST_COUNT = 100;

    @Autowired
    public AlertAlarmListServiceImpl(AlertHistoryRepository alertHistoryRepository) {
        this.alertHistoryRepository = alertHistoryRepository;
    }

    @Override
    public Map<String, Object> alertPageHistory(Integer pageNumber, String startDate, String endDate, String severity, String category, String resource, String target, int pagePostCount) {

        Specification<AlertHistory> spec = Specification.where(searchHistory(startDate, endDate, severity, category, resource, target));
        Pageable pageable = PageRequest.of(pageNumber-1, pagePostCount > 0 ? pagePostCount : 100, Sort.by(Sort.Direction.DESC, "updateTime"));

        Page<AlertHistory> page =  alertHistoryRepository.findAll(spec, pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("histories", page.getContent());
        map.put("totalCount", page.getTotalElements());

        return map;
    }

    @Override
    public Map<String, Object> getPageList(Integer curPageNum, Long total_Count) {

        AlertListPaginator alertListPaginator = new AlertListPaginator(BLOCK_PAGE_NUMBER_COUNT, PAGE_POST_COUNT, total_Count);
        return alertListPaginator.getElasticBolock(curPageNum);
    }

    @Override
    public Map<String, Object> alertSearchHistory(String startDate, String endDate, String severity, String category, String resource, String target) {

        Specification<AlertHistory> spec = Specification.where(searchHistory(startDate, endDate, severity, category, resource, target));
        Pageable pageable = PageRequest.of(0, PAGE_POST_COUNT, Sort.by(Sort.Direction.DESC, "updateTime"));

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
                map.put("updateTime", endDate);
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

            List<Predicate> predicates = getPredicateWithKeyword(map, root, criteriaBuilder);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<Predicate> getPredicateWithKeyword(Map<String, Object> searchKeyword, Root<AlertHistory> root, CriteriaBuilder builder){
        List<Predicate> predicates = new ArrayList<>();
        for (String key : searchKeyword.keySet()){
            if(searchKeyword.get(key) != null && "target".equals(key) && !"".equals(searchKeyword.get(key))) { // Like 검색
                predicates.add(builder.like(root.get(key),"%"+searchKeyword.get(key)+"%"));
            } else if("severity".equals(key)){
                predicates.add(builder.equal(root.get(key), searchKeyword.get(key)));
            } else if("category".equals(key) || "resource".equals(key)){
                predicates.add(builder.equal(root.get("alertRuleId").get(key), searchKeyword.get(key)));
            } else if("updateTime".equals(key)){
                if(searchKeyword.get(key) != null) {
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.US);
                    LocalDateTime createDate = LocalDateTime.parse(searchKeyword.get("createTime").toString(), dateFormat);
                    LocalDateTime updateDate = LocalDateTime.parse(searchKeyword.get("updateTime").toString(), dateFormat);

                    predicates.add(builder.between(root.get(key), createDate, updateDate));
                }
            } else {
                log.info("Not reachable for now -> {}", key);
            }
        }
        predicates.add(builder.equal(root.get("resolved"), 0));
        return predicates;
    }
}
