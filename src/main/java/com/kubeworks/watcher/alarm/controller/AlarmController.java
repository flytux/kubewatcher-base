package com.kubeworks.watcher.alarm.controller;

import com.kubeworks.watcher.alarm.service.AlertRuleConfigService;
import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.data.entity.AlertRule;
import com.kubeworks.watcher.data.entity.AlertRuleId;
import com.kubeworks.watcher.data.vo.AlertCategory;
import com.kubeworks.watcher.data.vo.AlertResource;
import com.kubeworks.watcher.data.vo.AlertType;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/setting/alarm")
@AllArgsConstructor(onConstructor_={@Autowired})
public class AlarmController implements BaseController {

    private static final long MENU_ID = 500;

    private static final String VIEW_NAME = "setting-alarm-list";

    private final PageViewService pageViewService;
    private final AlertRuleConfigService alertRuleConfigService;

    @ModelAttribute("alertRule")
    public AlertRule alertRule() {

        final AlertRule alertRule = new AlertRule();
        alertRule.setAlertRuleId(new AlertRuleId(AlertType.METRIC, AlertCategory.NODE, AlertResource.CPU));

        return alertRule;
    }

    @GetMapping(value="/list")
    public String alertConfigList(final Model model) {

        model.addAttribute(Props.PAGE, pageViewService.getPageView(MENU_ID));
        model.addAttribute("alertConfigList", alertRuleConfigService.alertRules());

        return createViewName(VIEW_NAME);
    }

    @GetMapping(value="/rule")
    public String alertConfig() {
        return createViewName(VIEW_NAME, Props.MODAL_CONTENTS);
    }

    @GetMapping(value = "/rule/{ruleId}")
    public String alertConfig(@PathVariable final Long ruleId, final Model model) {

        model.addAttribute("editMode", Boolean.TRUE);
        model.addAttribute("alertRule", alertRuleConfigService.alertRule(ruleId));

        return createViewName(VIEW_NAME, Props.MODAL_CONTENTS);
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "setting/alarm/";
    }
}
