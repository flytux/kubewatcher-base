package com.kubeworks.watcher.alert.rule.config.controller;

import com.kubeworks.watcher.alert.rule.config.AlertRuleConfigService;
import com.kubeworks.watcher.data.entity.KwAlertRule;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class AlertRuleConfigController {

    private final AlertRuleConfigService alertRuleConfigService;
    private final PageViewService pageViewService;


    @GetMapping(value = "/setting/alarm/list", produces = MediaType.TEXT_HTML_VALUE)
    public String alertConfigList(Model model) {

        List<KwAlertRule> alertConfigList = alertRuleConfigService.getAlertRuleList();
        model.addAttribute("alertConfigList", alertConfigList);

        Page page = pageViewService.getPageView(500);
        model.addAttribute("page", page);

        return "setting/alarm/setting-alarm-list";
    }
}
