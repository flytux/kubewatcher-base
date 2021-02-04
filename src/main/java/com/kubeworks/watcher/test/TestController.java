package com.kubeworks.watcher.test;

import com.kubeworks.watcher.ecosystem.grafana.dto.Dashboard;
import com.kubeworks.watcher.ecosystem.grafana.service.GrafanaSerivce;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class TestController {

    private final GrafanaSerivce grafanaSerivce;

    @GetMapping(value = "/grafana/dashboards")
    public String grafanaDashboards(Model model) {
        List<Dashboard> dashBoards = grafanaSerivce.dashboards();
        model.addAttribute("dashboards", dashBoards);
        return "grafana-dashboard";
    }


    @RequestMapping(value = "/application/usage/bootstrap-datetimepicker", method = RequestMethod.GET)
    public String usageOverview(Model model) {

        return "application/usage/bootstrap-datetimepicker";
    }

    @RequestMapping(value = "/application/usage/bootstrap-datetimepicker/betweenDate", method = RequestMethod.POST)
    public String betweenDate(Model model, RedirectAttributes redirectAttributes, @RequestParam String startDate, @RequestParam String endDate) {
        redirectAttributes.addFlashAttribute("startDate", startDate);
        redirectAttributes.addFlashAttribute("endDate", "2022-02-03");
        return "redirect:/application/usage/bootstrap-datetimepicker";
    }

}

