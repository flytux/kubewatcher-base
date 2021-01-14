package com.kubeworks.watcher.ecosystem.loki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Getter @Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class LokiResponse {

    String status;
    LokiApiData data;

    @Getter @Setter
    @FieldDefaults(level= AccessLevel.PRIVATE)
    public static class LokiApiData {

        @JsonProperty("resultType")
        String resultType;
        List<LokiApiResult> result;

        @Getter @Setter
        @FieldDefaults(level= AccessLevel.PRIVATE)
        public static class LokiApiResult {

            Map<String, String> metric;
            List<Object> value;

        }
    }

}
