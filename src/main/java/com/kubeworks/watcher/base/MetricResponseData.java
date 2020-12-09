package com.kubeworks.watcher.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MetricResponseData {

    public MetricResponseData(List<MetricResult> result) {
        this.result = result;
    }

    String resultType = "vector";
    List<MetricResult> result;

    @Getter @Setter @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MetricResult {

        @Builder
        private MetricResult(Map<String, String> metric,
                             List<Object> value) {
            this.metric = metric;
            this.value = value;
        }

        Map<String, String> metric;
        List<Object> value;
    }

}
