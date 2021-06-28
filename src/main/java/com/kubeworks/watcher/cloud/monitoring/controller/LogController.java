package com.kubeworks.watcher.cloud.monitoring.controller;

import com.kubeworks.watcher.base.BaseController;
import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.ecosystem.loki.LokiFeignClient;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
import com.kubeworks.watcher.preference.service.PageViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.time.ZoneId;

@Controller
@RequestMapping(value="/monitoring")
public class LogController implements BaseController {

    private static final long LOGGING_MENU_ID = 1128;

    private final LokiFeignClient client;

    private final PageViewService pageViewService;
    private final MonitoringProperties properties;
    private final ApplicationService applicationService;

    @Autowired
    public LogController(final LokiFeignClient client, final PageViewService pageViewService,
                         final MonitoringProperties properties, final ApplicationService applicationService) {
        this.client = client; this.pageViewService = pageViewService;
        this.properties = properties; this.applicationService = applicationService;
    }

    @GetMapping(value="/logging")
    public String logging(final Model model) {

        model.addAttribute(Props.HOST, properties.getDefaultCluster().getLoki().getUrl());
        model.addAttribute(Props.PAGE, pageViewService.getPageView(LOGGING_MENU_ID));
        model.addAttribute("services", applicationService.getManagementByName());
        model.addAttribute("applicationValue", applicationService.getServiceNamesLoki());

        return createViewName("logging");
    }

    // TODO Rest API로 server에서 ErrorCount 구하기
    @ResponseBody
    @GetMapping(value="/apiCall")
    public String lokiApiCall(@RequestParam(value="param") final String param) {

        // TODO 코드수정함 확인 필요
        final String padding = "000000";
        final Instant current = Instant.now();
        final String st = current.atZone(ZoneId.systemDefault()).minusHours(1).toInstant().toEpochMilli() + padding;
        final String en = current.toEpochMilli() + padding;
        final String q = "sum(count_over_time({app=~" + param.replace(',', '|') + "} |=" + "\"error\"" + "[1m])) by (app)";

        return client.requestQueryRange(q, Long.valueOf(st), Long.valueOf(en), 60L);
    }

    @Override
    public String retrieveViewNamePrefix() {
        return "monitoring/logging/";
    }
}
