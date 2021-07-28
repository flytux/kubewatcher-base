package com.kubeworks.watcher.alarm.controller;

import com.kubeworks.watcher.alarm.service.AlertAlarmListService;
import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping(value="/monitoring/alarm")
@AllArgsConstructor(onConstructor_={@Autowired})
public class MonitoringAlarmController implements BaseController {

    private static final long MENU_ID = 150;
    private static final String VIEW_NAME = "alarm-list";

    private final PageViewService pageViewService;
    private final AlertAlarmListService alertAlarmListService;

    @GetMapping(value="/list")
    public String alertAlarmList(final Model model, @RequestParam(value="page", defaultValue="1") final Integer pageNumber) {

        model.addAttribute(Props.PAGE, pageViewService.getPageView(MENU_ID));

        final Map<String, Object> resultMap = alertAlarmListService.alertPageHistory(pageNumber, null, null, null, null, null, null, 0);

        model.addAttribute("alertHistoryList", resultMap.get("histories"));
        model.addAttribute("pageList", alertAlarmListService.getPageList(pageNumber, (Long)resultMap.get("totalCount")));

        return createViewName(VIEW_NAME);
    }

    @GetMapping(value="/list/pageMove")
    public String alertAlarmPageList(final Model model, @RequestParam(value="page", defaultValue="1") final Integer pageNumber,
            @RequestParam final String startDate, @RequestParam final String endDate,
            @RequestParam final String severity, @RequestParam final String category,
            @RequestParam final String resource, @RequestParam final String target) {

        final Map<String, Object> resultMap = alertAlarmListService.alertPageHistory(pageNumber, startDate, endDate, severity, category, resource, target, 0);

        model.addAttribute("alertHistoryList", resultMap.get("histories"));
        model.addAttribute("pageList", alertAlarmListService.getPageList(pageNumber, (Long)resultMap.get("totalCount")));

        return createViewName(VIEW_NAME, " :: alarmList");
    }

    @GetMapping(value="/list/search")
    public String alarmSearch(final Model model,
            @RequestParam final String startDate, @RequestParam final String endDate,
            @RequestParam final String severity, @RequestParam final String category,
            @RequestParam final String resource, @RequestParam final String system) {

        final Map<String, Object> resultMap = alertAlarmListService.alertSearchHistory(startDate, endDate, severity, category, resource, system);
        model.addAttribute("alertHistoryList", resultMap.get("histories"));

        final Map<String, Object> pageList = alertAlarmListService.getPageList(1, (Long)resultMap.get("totalCount"));
        model.addAttribute("pageList", pageList);

        return createViewName(VIEW_NAME, " :: alarmList");
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "monitoring/alarm/";
    }
}
