package com.kubeworks.watcher.cloud.usage;

import com.google.common.collect.ImmutableMap;
import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.base.MetricResponseData;
import com.kubeworks.watcher.config.properties.ApplicationServiceProperties;
import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.vo.ClusterPodUsage;
import com.kubeworks.watcher.data.vo.UsageMetricType;
import com.kubeworks.watcher.ecosystem.kubernetes.service.MetricService;
import com.kubeworks.watcher.preference.service.PageConstants;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping(path = "/api/v1")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class UsageRestController {

    private static final long USAGE_MENU_ID = 1127;

    private final ApplicationServiceProperties applicationServiceProperties;
    private final SpringTemplateEngine springTemplateEngine;

    private final MetricService metricService;
    private final PageViewService pageViewService;


    @GetMapping(value = "/application/usage/usage-overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> usageOverview() {
        LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime end = start.plusDays(1).minusNanos(1);
        List<ClusterPodUsage> usages = metricService.usages(null, start, end);

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        return builder.put("searchDate", start)
            .put("namespaces", applicationServiceProperties.getNamespaces())
            .put("usages", usages)
            .put("link", PageConstants.API_URL_BY_NAMESPACED_USAGE)
            .build();
    }

    @GetMapping(value = "/application/usage/usage-overview/namespace/{namespace}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> usageOverview(@PathVariable String namespace, @RequestParam LocalDate searchDate) {

        LocalDateTime searchDateTime = searchDate.atStartOfDay();
//        start = start.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime end = searchDateTime.plusDays(1).minusNanos(1);
        List<ClusterPodUsage> usages = metricService.usages(namespace, searchDateTime, end);
        return ImmutableMap.of("usages", usages);
    }

    /**
     * detail modal // TODO - sql modal
     */
    @GetMapping(value = "/application/usage/detail/namespace/{namespace}/application/{application}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> detailUsagePage(@PathVariable String namespace, @PathVariable String application,
                                               @RequestParam LocalDate searchDate) {
        Page page = pageViewService.getPageView(USAGE_MENU_ID);
        Map<String, Object> response = new HashMap<>(2);
        response.put("namespace", namespace);
        response.put("application", application);
        response.put("searchDate", searchDate);
        response.put("unit", ChronoUnit.DAYS);
        response.put("page", page);
        String describeHtml = springTemplateEngine.process("application/usage/usage-overview",
            Collections.singleton("modalContents"), new Context(Locale.KOREA, response));
        response.put("describe", describeHtml);
        return response;
    }

    @GetMapping(value = "/application/usage/metric/namespace/{namespace}/application/{application}/metric/{usageMetricType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<MetricResponseData> usageMetrics(@PathVariable String namespace,
                                                        @PathVariable String application,
                                                        @PathVariable UsageMetricType usageMetricType,
                                                        @RequestParam ChronoUnit unit,
                                                        @RequestParam LocalDate searchDate) {
        return metricService.usageMetrics(namespace, application, usageMetricType, unit, searchDate.atStartOfDay());
    }

}
