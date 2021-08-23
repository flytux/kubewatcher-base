package com.kubeworks.watcher.alarm.controller;

import com.kubeworks.watcher.alarm.service.AlertAlarmListService;
import com.kubeworks.watcher.alarm.service.AlertRuleConfigService;
import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.data.entity.AlertRule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path="/api/v1")
@AllArgsConstructor(onConstructor_={@Autowired})
public class AlarmRestController {

    private final AlertAlarmListService alertAlarmListService;
    private final AlertRuleConfigService alertRuleConfigService;

    @PostMapping(value="/alarm/rule")
    public ApiResponse<String> registrationAlarmRule(@RequestBody AlertRule alertRule) {
        return alertRuleConfigService.registrationAlarmRule(alertRule);
    }

    @PutMapping(value="/alarm/rule")
    public ApiResponse<String> updateAlarmRule(@RequestBody AlertRule alertRule) {

        try {
            return alertRuleConfigService.updateAlarmRule(alertRule);
        } catch (DataIntegrityViolationException e) {
            log.warn("", e);

            ApiResponse<String> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("이미 등록된 Rule 입니다.");
            return response;
        }
    }

    @PostMapping(value="/setting/alarm/rule/delete")
    public ApiResponse<String> deleteAlertRule(@RequestBody AlertRule alertRule) {
        return alertRuleConfigService.deleteAlarmRule(alertRule);
    }

    @PutMapping(value="/list/alarm/Check")
    public ApiResponse<String> alramCheck(@RequestParam("historyId") Long historyId) {
        return alertAlarmListService.alertCheck(historyId);
    }
}
