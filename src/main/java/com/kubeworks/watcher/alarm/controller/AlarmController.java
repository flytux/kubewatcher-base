package com.kubeworks.watcher.alarm.controller;

import com.kubeworks.watcher.alarm.service.AlertRuleConfigService;
import com.kubeworks.watcher.data.entity.AlertRule;
import com.kubeworks.watcher.data.entity.AlertRuleId;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.vo.AlertCategory;
import com.kubeworks.watcher.data.vo.AlertResource;
import com.kubeworks.watcher.data.vo.AlertType;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class AlarmController {

    private final AlertRuleConfigService alertRuleConfigService;
    private final PageViewService pageViewService;

    @ModelAttribute("alertRule")
    public AlertRule alertRule() {
        AlertRule alertRule = new AlertRule();
        alertRule.setAlertRuleId(new AlertRuleId(AlertType.METRIC, AlertCategory.NODE, AlertResource.CPU));
        return alertRule;
    }

    @GetMapping(value = "/setting/alarm/list", produces = MediaType.TEXT_HTML_VALUE)
    public String alertConfigList(Model model) {

        List<AlertRule> alertConfigList = alertRuleConfigService.alertRules();
        model.addAttribute("alertConfigList", alertConfigList);

        Page page = pageViewService.getPageView(500);
        model.addAttribute("page", page);

        return "setting/alarm/setting-alarm-list";
    }

    @GetMapping(value = "/setting/alarm/rule", produces = MediaType.TEXT_HTML_VALUE)
    public String alertConfig(Model model) {
        return "setting/alarm/setting-alarm-list :: modalContents";
    }

    @GetMapping(value = "/setting/alarm/rule/{ruleId}", produces = MediaType.TEXT_HTML_VALUE)
    public String alertConfig(Model model, @PathVariable long ruleId) {
        AlertRule alertRule = alertRuleConfigService.alertRule(ruleId);
        model.addAttribute("alertRule", alertRule);
        model.addAttribute("editMode", true);
        return "setting/alarm/setting-alarm-list :: modalContents";
    }



}
