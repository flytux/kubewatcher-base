package com.kubeworks.watcher.cloud.usage;

import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.ecosystem.kubernetes.service.MetricService;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
import com.kubeworks.watcher.preference.service.PageConstants;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping(value="/application/usage")
@AllArgsConstructor(onConstructor_={@Autowired})
public class UsageController implements BaseController {

    private static final String VIEW_NAME = "usage-overview";

    private final MetricService metricService;
    private final ApplicationService applicationService;

    @GetMapping(value="/usage-overview")
    public String usageOverview(final Model model) {

        final LocalDateTime st = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        model.addAttribute("searchDate", st);
        model.addAttribute("namespaces", applicationService.getNamespaces());
        model.addAttribute("usages", metricService.usages(null, st, st.plusDays(1).minusNanos(1)));
        model.addAttribute("link", PageConstants.API_URL_BY_NAMESPACED_USAGE);

        return createViewName(VIEW_NAME);
    }

    @GetMapping(value="/usage-overview/namespace/{namespace}")
    public String usageOverview(final Model model, @PathVariable final String namespace, @RequestParam final LocalDate searchDate) {

        final LocalDateTime st = searchDate.atStartOfDay();
        model.addAttribute("usages", metricService.usages(namespace, st, st.plusDays(1).minusNanos(1)));

        return createViewName(VIEW_NAME, Props.CONTENT_LIST);
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "application/usage/";
    }
}
