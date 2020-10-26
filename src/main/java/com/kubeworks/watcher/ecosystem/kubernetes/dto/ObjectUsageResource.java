package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ObjectUsageResource {

    String namespace;

    String name;

    String status;

    Map<String, Quantity> requests;
    Map<String, Quantity> limits;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    @JsonIgnore
    public Map<String, Quantity> computeRequests() {
        if (requests == null) {
            requests = new HashMap<>();
        }
        return requests;
    }

    @JsonIgnore
    public Map<String, Quantity> computeLimits() {
        if (limits == null) {
            limits = new HashMap<>();
        }
        return limits;
    }

    public Quantity getRequestsMetric(String metricName) {
        if (requests == null) {
            return new Quantity("0");
        }
        return requests.getOrDefault(metricName, new Quantity("0"));
    }

    public Quantity getLimitsMetric(String metricName) {
        if (limits == null) {
            return new Quantity("0");
        }
        return limits.getOrDefault(metricName, new Quantity("0"));
    }
}
