package com.kubeworks.watcher.ecosystem;

import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.ecosystem.kubernetes.serdes.CustomQuantityFormatter;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.QuantityFormatter;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.http.HttpStatus;

import javax.servlet.http.Cookie;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class ExternalConstants {

    public final String GRAFANA_API_PREFIX = "/api";

    public final String PROMETHEUS_QUERY_STRING_PREFIX = "?query=";
    public final String PROMETHEUS_QUERY_API_URI = GRAFANA_API_PREFIX + "/v1/query" + PROMETHEUS_QUERY_STRING_PREFIX;
    public final String PROMETHEUS_RANGE_QUERY_API_URI = GRAFANA_API_PREFIX + "/v1/query_range" + PROMETHEUS_QUERY_STRING_PREFIX;

    public final Pattern GRAFANA_TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\$\\w+");
    public final String SUCCESS_STATUS_STR = "success";
    public final String NONE_STR = "None";

    public final String UNKNOWN = "Unknown";
    public final String UNKNOWN_DASH = "-";
    public final String NONE = "<None>";
    public final String NODE_ROLE_KUBERNETES_IO = "node-role.kubernetes.io/";
    public final String KUBERNETES_IO_ROLE = "kubernetes.io/role";

    public final int DEFAULT_K8S_CLIENT_TIMEOUT_SECONDS = 3;
    public final int DEFAULT_K8S_OBJECT_LIMIT = 500;

    public final String EVENT_FIELD_SELECTOR_KIND = "involvedObject.kind=";
    public final String EVENT_FIELD_SELECTOR_INVOLVED_OBJECT_UID_KEY = "involvedObject.uid=";
    public final String EVENT_FIELD_SELECTOR_INVOLVED_OBJECT_NAME_KEY = "involvedObject.name=";
    public final String EVENT_FIELD_SELECTOR_INVOLVED_OBJECT_NAMESPACE_KEY = "involvedObject.namespace=";

    public final PeriodFormatter DEFAULT_PERIOD_FORMATTER = new PeriodFormatterBuilder()
        .appendDays().appendSuffix("d ")
        .appendHours().appendSuffix("h ")
        .appendMinutes().appendSuffix("m ")
        .appendSeconds().appendSuffix("s ")
        .appendMillis().appendSuffix("ms")
        .printZeroNever()
        .toFormatter();

    public List<String> getTemplateVariables(String query) {
        List<String> result = null;
        Matcher matcher = GRAFANA_TEMPLATE_VARIABLE_PATTERN.matcher(query);
        while (matcher.find()) {
            if (result == null) {
                result = new ArrayList<>();
            }
            result.add(matcher.group().replace("$", ""));
        }
        return result != null ? result : Collections.emptyList();
    }

    public boolean isSuccessful(int status) {
        return HttpStatus.valueOf(status).is2xxSuccessful();
    }

    public String getFormatDuration(Duration duration) {
        if (duration == null) {
            return ExternalConstants.UNKNOWN_DASH;
        }
        return DEFAULT_PERIOD_FORMATTER.print(duration.toPeriod());
    }

    public String getCurrentBetweenPeriod(long durationInMillis) {
        return getBetweenPeriod(durationInMillis, System.currentTimeMillis());
    }

    public String getBetweenPeriod(long startDurationInMillis, long endDurationInMillis) {
        return DEFAULT_PERIOD_FORMATTER.print(new Period(endDurationInMillis - startDurationInMillis));
    }

    public String getBetweenPeriodDay(long startDurationInMillis) {
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - startDurationInMillis);
        return days + "d";
    }

    public Map<String, PageRowPanel> thymeleafConvertList2Map(Collection<PageRowPanel> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(PageRowPanel::getTitle, Function.identity()));
    }

    private final CustomQuantityFormatter QUANTITY_FORMATTER = new CustomQuantityFormatter();
    private final QuantityFormatter K8S_QUANTITY_FORMATTER = new QuantityFormatter();

    public String toStringQuantity(Quantity quantity) {
        return QUANTITY_FORMATTER.format(quantity);
    }

    public String toStringQuantityViaK8s(Quantity quantity) {
        return K8S_QUANTITY_FORMATTER.format(quantity);
    }

    public Quantity addQuantity(Quantity quantity1, Quantity quantity2) {
        return new Quantity(quantity1.getNumber().add(quantity2.getNumber()), quantity1.getFormat());
    }

    public String thymeleafCookieGetThemeCss(Cookie[] list) {

        String theme = "/assets/css/style_dark.css";

        for (final Cookie cookie : list) {
            if ("theme".equals(cookie.getName())) {
                if ("white".equals(cookie.getValue())) {
                    theme = "/assets/css/style.css";
                }
                break;
            }
        }

        return theme;
    }
}
