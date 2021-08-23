package com.kubeworks.watcher.ecosystem.prometheus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@ToString
@Getter @Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class PrometheusApiResponse {

    String status;
    PrometheusApiData data;

    @ToString
    @Getter @Setter
    @FieldDefaults(level= AccessLevel.PRIVATE)
    public static class PrometheusApiData {

        @JsonProperty("resultType")
        String resultType;
        List<PrometheusApiResult> result = Collections.emptyList();

        @ToString
        @Getter @Setter
        @FieldDefaults(level= AccessLevel.PRIVATE)
        public static class PrometheusApiResult {

            Map<String, String> metric = Collections.emptyMap();
            List<Object> value = Collections.emptyList();
            List<Object> values = Collections.emptyList();
        }
    }
}
