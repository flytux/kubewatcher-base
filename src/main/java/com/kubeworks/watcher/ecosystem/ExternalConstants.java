package com.kubeworks.watcher.ecosystem;

import com.kubeworks.watcher.ecosystem.grafana.dto.TemplateVariable;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ExternalConstants {

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
}
