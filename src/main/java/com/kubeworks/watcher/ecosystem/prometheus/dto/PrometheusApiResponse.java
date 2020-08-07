package com.kubeworks.watcher.ecosystem.prometheus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Getter @Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class PrometheusApiResponse {

    String status;
    PrometheusApiData data;

    @Getter @Setter
    @FieldDefaults(level= AccessLevel.PRIVATE)
    public static class PrometheusApiData {

        @JsonProperty("resultType")
        String resultType;
        List<PrometheusApiResult> result;

        @Getter @Setter
        @FieldDefaults(level= AccessLevel.PRIVATE)
        public static class PrometheusApiResult {

            Map<String, String> metric;
            List<Object> value;

        }

    }
}