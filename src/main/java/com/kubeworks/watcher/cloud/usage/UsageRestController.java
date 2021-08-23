package com.kubeworks.watcher.cloud.usage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.kubeworks.watcher.base.ApiResponse;
import com.kubeworks.watcher.base.MetricResponseData;
import com.kubeworks.watcher.data.entity.ApplicationManagement;
import com.kubeworks.watcher.data.vo.UsageMetricType;
import com.kubeworks.watcher.ecosystem.kubernetes.service.MetricService;
import com.kubeworks.watcher.ecosystem.prometheus.service.ApplicationService;
import com.kubeworks.watcher.preference.service.ManagementService;
import com.kubeworks.watcher.preference.service.PageConstants;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor(onConstructor_={@Autowired})
@RequestMapping(path="/api/v1/application/usage")
public class UsageRestController {

    private static final long USAGE_MENU_ID = 1127;

    private final SpringTemplateEngine springTemplateEngine;

    private final MetricService metricService;
    private final PageViewService pageViewService;

    private final ManagementService managementService;
    private final ApplicationService applicationService;

    @GetMapping(value="/usage-overview")
    public Map<String, Object> usageOverview() {

        final LocalDateTime st = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        return ImmutableMap.<String, Object>builder().put("searchDate", st)
            .put("namespaces", applicationService.getNamespaces())
            .put("usages", metricService.usages(null, st, st.plusDays(1).minusNanos(1)))
            .put("link", PageConstants.API_URL_BY_NAMESPACED_USAGE)
            .build();
    }

    @GetMapping(value="/usage-overview/namespace/{namespace}")
    public Map<String, Object> usageOverview(@PathVariable final String namespace, @RequestParam final LocalDate searchDate) {

        final LocalDateTime st = searchDate.atStartOfDay();

        return ImmutableMap.of("usages", metricService.usages(namespace, st, st.plusDays(1).minusNanos(1)));
    }

    // TODO - sql modal
    @GetMapping(value="/detail/namespace/{namespace}/application/{application}")
    public Map<String, Object> detailUsagePage(@PathVariable final String namespace, @PathVariable final String application, @RequestParam final LocalDate searchDate) {

        final Map<String, Object> response = Maps.newHashMapWithExpectedSize(7);

        response.put("namespace", namespace);
        response.put("application", application);
        response.put("searchDate", searchDate);
        response.put("unit", ChronoUnit.DAYS);
        response.put("page", pageViewService.getPageView(USAGE_MENU_ID));
        response.put("serviceName", retrieveServiceName(namespace, application));
        response.put("describe", springTemplateEngine.process("application/usage/usage-overview", Collections.singleton("modalContents"), new Context(Locale.KOREA, response)));

        return response;
    }

    @GetMapping(value="/metric/namespace/{namespace}/application/{application}/metric/{usageMetricType}")
    public ApiResponse<MetricResponseData> usageMetrics(@PathVariable final String namespace,
                                                        @PathVariable final String application,
                                                        @PathVariable final UsageMetricType usageMetricType,
                                                        @RequestParam final ChronoUnit unit,
                                                        @RequestParam final LocalDate searchDate) {

        return metricService.usageMetrics(namespace, application, usageMetricType, unit, searchDate.atStartOfDay());
    }

    private String retrieveServiceName(final String namespace, final String application) {
        return Optional.ofNullable(managementService.managementServiceWithDefault(namespace, application)).map(ApplicationManagement::getDisplayName).orElse(application);
    }
}
