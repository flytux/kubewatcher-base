package com.kubeworks.watcher.alarm.controller;

import com.kubeworks.watcher.alarm.service.AlertRuleConfigService;
import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.AlertRule;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class AlarmRestController {

    private final AlertRuleConfigService alertRuleConfigService;

    @PostMapping(value = "/alarm/rule", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> registrationAlarmRule(@RequestBody AlertRule alertRule) {
        return alertRuleConfigService.registrationAlarmRule(alertRule);
    }

    @PutMapping(value = "/alarm/rule", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<String> updateAlarmRule(@RequestBody AlertRule alertRule) {
        try {
            return alertRuleConfigService.updateAlarmRule(alertRule);
        } catch (DataIntegrityViolationException e) {
            ApiResponse<String> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("이미 등록된 Rule 입니다.");
            return response;
        }
    }
}
