package com.kubeworks.watcher.ecosystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.ecosystem.grafana.dto.TemplateVariable;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RegExUtils;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.http.HttpStatus;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class ExternalConstants {

    public ObjectMapper OBJECT_MAPPER;

    public static void setObjectMapper(ObjectMapper objectMapper) {
        ExternalConstants.OBJECT_MAPPER = objectMapper;
    }

    private Yaml yaml;

    public static void setSnakeyaml(Yaml yaml) {
        ExternalConstants.yaml = yaml;
    }

    public String yamlDump(Object data) {
        if (data == null) {
            return "";
        }
        return yaml.dump(data);
    }

    public String yamlDumpHtml(Object data) {
        String yamlDump = ExternalConstants.yamlDump(data);
        return RegExUtils.replaceAll(
            RegExUtils.replaceAll(yamlDump, " ", "&nbsp;"),
            "\n", "<br/>");
    }

    public final String GRAFANA_API_PREFIX = "/api";
    public final String GRAFANA_DASHBOARD_ALL_SEARCH_URI = GRAFANA_API_PREFIX + "/search?type=dash-db";
    public final String GRAFANA_FOLDER_ALL_SEARCH_URI = GRAFANA_API_PREFIX + "/search?type=dash-folder";
    public final String GRAFANA_DASHBOARD_GET_URI_PATTERN = GRAFANA_API_PREFIX + "/dashboards/uid/{uid}";

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

    public final String REQUEST_HEADERS_BY_ACCEPT_TABLE_VALUE = "application/json;as=Table;v=v1;g=meta.k8s.io,application/json;as=Table;v=v1beta1;g=meta.k8s.io,application/json";
    public final int DEFAULT_K8S_CLIENT_TIMEOUT_SECONDS = 3;
    public final int DEFAULT_K8S_OBJECT_LIMIT = 500;

    public final String EVENT_FIELD_SELECTOR_KIND = "involvedObject.kind=";
    public final String EVENT_FIELD_SELECTOR_INVOLVED_OBJECT_UID_KEY = "involvedObject.uid=";
    public final String EVENT_FIELD_SELECTOR_INVOLVED_OBJECT_NAME_KEY = "involvedObject.name=";
    public final String EVENT_FIELD_SELECTOR_INVOLVED_OBJECT_NAMESPACE_KEY = "involvedObject.namespace=";

    public final PeriodFormatter DEFAULT_PERIOD_FORMATTER = new PeriodFormatterBuilder()
        .appendDays().appendSuffix("d")
        .appendHours().appendSuffix("h")
        .appendMinutes().appendSuffix("m")
        .appendSeconds().appendSuffix("s")
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

    public int compareSortTemplateVariables(TemplateVariable o1, TemplateVariable o2) {
        int compare = Integer.compare(o1.getRefFields().size(), o2.getRefFields().size());
        if (compare == 0) {
            return o2.getRefFields().contains(o1.getName()) ? -1 : 0;
        }
        return compare;
    }

    public boolean isSuccessful(int status) {
        return HttpStatus.valueOf(status).is2xxSuccessful();
    }


    public String getCurrentBetweenPeriod(long durationInMillis) {
        return getBetweenPeriod(durationInMillis, System.currentTimeMillis());
    }

    public String getBetweenPeriod(long startDurationInMillis, long endDurationInMillis) {
        return DEFAULT_PERIOD_FORMATTER.print(new Period(endDurationInMillis - startDurationInMillis));
    }

    public String getBetweenPeriodDay(long startDurationInMillis) {
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()-startDurationInMillis);
        return days+"d";
    }

    public Map<String, PageRowPanel> thymeleafConvertList2Map(List<PageRowPanel> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(PageRowPanel::getTitle, panel -> panel));
    }

}
