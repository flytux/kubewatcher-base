package com.kubeworks.watcher.ecosystem.grafana.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

@Getter @Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
@NoArgsConstructor
@ToString
public class TemplateVariable {

    String label;
    String name;
    String query;
    String type; // TODO enum 처리 (참고 : https://grafana.com/docs/grafana/latest/variables/templates-and-variables/)
    int hide;

    @JsonProperty("includeAll")
    boolean includeAll;

    @JsonProperty("allValue")
    String allValueRegex;

    /** 커스텀 설정 **/
    String queryType;
    List<String> values;
    List<String> refFields;
    String regex;


    /*
     * TODO
     *  1. sorting : 템플릿 내부 변수 갯수로 오름차순(asc) 정렬(단일 조회(외부 변수 없는) 항목부터)
     *  2. refFields : 내부 변수명 추출(해당 값으로 실제 값을 가져옴)
     *  3. setValues : 2에 정의된 정보를 기준으로 데이터 조회 : 조회 대상의 데이터가 없을 경우 재귀처리
     *    - 예외처리 : 2개 이상의 변수가 바인딩될 경우 화면에서 처리함.
     *                 - 사유 : 2개의 변수가 복수의 데이터를 가질 수 있으므로 성능 및 복잡도가 높아질 수 있음
     *                 - 단, 화면처리시 초기 화면은 read-only로 지
     */

    // TODO 템플릿 쿼리 유형 총 5가지 유형임.
    // 현재 버전에서는 label_values(metric, label)를 먼저 처리함.
    // 관련 : https://grafana.com/docs/grafana/latest/features/datasources/prometheus/
    public void setQuery(String query) {
        this.query = query;
        if (StringUtils.equalsIgnoreCase("label_names()", query)) {
            queryType = "labelNames";
        } else if (StringUtils.startsWithIgnoreCase(query, "label_values")) {
            queryType = query.contains(",") ? "singleLabelValues" : "allLabelValues";
        } else if (StringUtils.startsWithIgnoreCase(query, "metrics")) {
            queryType = "metrics";
        } else if (StringUtils.startsWithIgnoreCase(query, "query_result")) {
            queryType = "query_result";
        } else {
            queryType = "unknown";
        }
    }

    public String getApiQueryMetricLabel() {
        if (StringUtils.equals("singleLabelValues", queryType)) {
            return StringUtils.substring(query, query.lastIndexOf(',') + 1, query.lastIndexOf(')')).trim();
        }
        return StringUtils.EMPTY;
    }

    public String getApiQuery() {
        if (StringUtils.equals("singleLabelValues", queryType)) {
            return StringUtils.substring(query, query.indexOf("label_values(") + "label_values(".length(), query.lastIndexOf(',')).trim();
        }
        return query;
    }

    public List<String> getValues() {
        if (StringUtils.equalsIgnoreCase("interval", type)) {
            return Arrays.asList(query.split(","));
        }
        return values;
    }

    public void setRegex(String regex) {
        this.regex = StringUtils.substringBetween(regex, "/.*", ".*/");
    }
}
