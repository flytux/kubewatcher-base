package com.kubeworks.watcher.alarm.batch;

import com.kubeworks.watcher.config.properties.MonitoringProperties;
import com.kubeworks.watcher.data.entity.AlertHistory;
import com.kubeworks.watcher.data.entity.AlertRule;
import com.kubeworks.watcher.data.entity.AlertRuleId;
import com.kubeworks.watcher.data.entity.AlertRuleMetric;
import com.kubeworks.watcher.data.repository.AlertHistoryRepository;
import com.kubeworks.watcher.data.repository.AlertRuleMetricRepository;
import com.kubeworks.watcher.data.repository.AlertRuleRepository;
import com.kubeworks.watcher.data.vo.AlertCategory;
import com.kubeworks.watcher.data.vo.AlertResource;
import com.kubeworks.watcher.data.vo.AlertSeverity;
import com.kubeworks.watcher.ecosystem.ExternalConstants;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.EventDescribe;
import com.kubeworks.watcher.ecosystem.kubernetes.service.EventService;
import com.kubeworks.watcher.ecosystem.prometheus.dto.PrometheusApiResponse;
import com.kubeworks.watcher.ecosystem.prometheus.feign.PrometheusFeginClient;
import io.kubernetes.client.openapi.models.V1ObjectReference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class AlertMonitorManager {

    private final String clusterTotalMemoryByteApi = "/api/v1/query?query=sum(node_memory_MemTotal_bytes{device!~\"rootfs|HarddiskVolume.+\",zone!=\"external\"}) - sum(node_memory_MemAvailable_bytes{device!~\"rootfs|HarddiskVolume.+\",zone!=\"external\"})";
    private final long defaultStep = 60;
    private final ZoneId defaultZone = ZoneId.of("Asia/Seoul");

    private final MonitoringProperties monitoringProperties;
    private final PrometheusFeginClient prometheusFeginClient;
    private final AlertRuleRepository alertRuleRepository;
    private final AlertRuleMetricRepository alertRuleMetricRepository;
    private final AlertHistoryRepository alertHistoryRepository;
    private final EventService eventService;

    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Seoul")
    public void alertMonitoring() {

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<AlertRule> alertRules = alertRuleRepository.findAll();

        alertRules.stream()
            .filter(alertRule -> alertRule.getAlertRuleMetric() != null)
            .forEach(alertRule -> {
                List<AlertHistory> alerts = Collections.emptyList();
                AlertRuleMetric alertRuleMetric = alertRule.getAlertRuleMetric();
                AlertRuleId alertRuleId = alertRule.getAlertRuleId();
                switch (alertRuleId.getType()) {
                    case METRIC:
                        String apiUri = getApiUri(alertRule, alertRuleId, alertRuleMetric.getExpression());
                        PrometheusApiResponse queryRange = prometheusFeginClient.getQueryRange(apiUri, now.minusMinutes(alertRule.getDuration()).atZone(defaultZone).toEpochSecond(), now.atZone(defaultZone).toEpochSecond(), defaultStep);
                        if (queryRange.getData() == null) { return; }

                        String resultType = queryRange.getData().getResultType();
                        alerts = queryRange.getData().getResult().stream()
                            .filter(prometheusApiResult -> isMetricAlert(alertRule, resultType, prometheusApiResult))
                            .map(prometheusApiResult -> getMetricAlerts(alertRule, prometheusApiResult))
                            .collect(Collectors.toList());
                        break;
                    case LOG:
                        if (alertRuleId.getCategory() == AlertCategory.EVENT) {
                            List<EventDescribe> events = eventService.events();
                            alerts = events.stream()
                                .filter(eventDescribe -> isLogAlert(alertRule, eventDescribe))
                                .map(eventDescribe -> getLogAlerts(alertRule, eventDescribe))
                                .collect(Collectors.toList());
                        } else if (alertRuleId.getCategory() == AlertCategory.LOG) {
                            // no implementation
                        }

                        break;
                    default:
                        log.warn("unsupported alert type={}", alertRuleId.getType());
                        break;
                }
                List<AlertHistory> alertHistories = alertHistoryRepository.saveAll(alerts);
                log.info("size={}, alert = {}", alertHistories.size(), alertHistories);
            });

    }

    private AlertHistory getLogAlerts(AlertRule alertRule, EventDescribe eventDescribe) {
        String target = ExternalConstants.UNKNOWN;
        V1ObjectReference involvedObject = eventDescribe.getInvolvedObject();
        if (involvedObject != null) {
            target = String.join("/", involvedObject.getKind(), involvedObject.getName());
        }
        return AlertHistory.builder()
            .alertRuleId(alertRule.getAlertRuleId())
            .message(eventDescribe.getMessage())
            .target(target)
            .severity(alertRule.getSeverity())
            .build();
    }

    private boolean isLogAlert(AlertRule alertRule, EventDescribe eventDescribe) {
        String reason = eventDescribe.getReason();
        String message = eventDescribe.getMessage();
        return StringUtils.contains(reason, alertRule.getDetectString()) 
            || StringUtils.contains(message, alertRule.getDetectString());
    }

    private String getApiUri(AlertRule alertRule, AlertRuleId alertRuleId, String expression) {
        if (alertRuleId.getCategory() == AlertCategory.POD && alertRuleId.getResource() == AlertResource.MEMORY) {
            return String.format(expression, getClusterTotalMemoryBytesString(), alertRule.getWarningLevel());
        } else {
            return String.format(expression, alertRule.getWarningLevel());
        }
    }

    private boolean isMetricAlert(AlertRule alertRule, String resultType, PrometheusApiResponse.PrometheusApiData.PrometheusApiResult prometheusApiResult) {
        if (StringUtils.equalsAnyIgnoreCase(resultType, "matrix")) {
            List<Object> values = prometheusApiResult.getValues();
            if (values.size() < alertRule.getDuration()) {
                return false;
            }
            double average = getAverage(alertRule, values);
            return average >= alertRule.getWarningLevel();
        } else {
            return false;
        }
    }

    private double getAverage(AlertRule alertRule, List<Object> values) {
        return IntStream.range(values.size() - 1 == alertRule.getDuration() ? 1 : 0, values.size())
            .mapToObj(values::get)
            .collect(Collectors.summarizingDouble(value -> {
                List<?> valueArray = (List<?>) value;
                if (valueArray.get(1) instanceof String) {
                    return Double.parseDouble(String.valueOf(valueArray.get(1)));
                }
                return 0;
            })).getAverage();
    }

    private AlertHistory getMetricAlerts(AlertRule alertRule, PrometheusApiResponse.PrometheusApiData.PrometheusApiResult prometheusApiResult) {
        String targetName = prometheusApiResult.getMetric().getOrDefault(alertRule.getAlertRuleMetric().getMetricName(), ExternalConstants.UNKNOWN);
        double average = getAverage(alertRule, prometheusApiResult.getValues());
        final String message = String.format(alertRule.getAlertRuleMetric().getMessageTemplate(), average + "%", alertRule.getAlertRuleId().getResource().name());
        return AlertHistory.builder()
            .alertRuleId(alertRule.getAlertRuleId())
            .message(message)
            .target(targetName)
            .severity(average >= alertRule.getDangerLevel() ? AlertSeverity.DANGER : AlertSeverity.WARN)
            .build();
    }

    private String getPrometheusHost() {
        return monitoringProperties.getDefaultPrometheusUrl();
    }

    private String getClusterTotalMemoryBytesString() {
        try {
            PrometheusApiResponse prometheusApiResponse = prometheusFeginClient.getQuery(clusterTotalMemoryByteApi);
            if (prometheusApiResponse.getData() == null) {
                throw new IllegalAccessException("empty node_memory_MemTotal_bytes metrics");
            }
            Optional<Object> valueOptional = prometheusApiResponse.getData().getResult().stream()
                .map(result -> result.getValue().get(1)).findFirst();

            if (valueOptional.isPresent()) {
                String value = String.valueOf(valueOptional.get());
                return NumberUtils.isCreatable(value) ? value : "1";
            }
            return "1";
        } catch (Exception e) {
            log.error("failed get node_memory_MemTotal_bytes metrics", e);
        }
        return "1";
    }
}
