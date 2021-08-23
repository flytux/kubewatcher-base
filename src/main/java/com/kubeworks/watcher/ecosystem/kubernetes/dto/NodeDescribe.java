package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1NodeAddress;
import io.kubernetes.client.openapi.models.V1NodeCondition;
import io.kubernetes.client.openapi.models.V1NodeSystemInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NodeDescribe {

    @Builder
    private NodeDescribe(String name, String roles, Map<String, String> labels,
                         Map<String, String> annotations, DateTime creationTimestamp,
                         Map<String, String> taints, boolean unSchedulable,
                         List<V1NodeCondition> conditions, List<V1NodeAddress> addresses,
                         Map<String, Quantity> capacity, Map<String, Quantity> allocatable,
                         V1NodeSystemInfo systemInfo, String podCIDR, List<String> podCIDRs,
                         List<ObjectUsageResource> pods, ObjectUsageResource allocateResources, List<EventTable> events) {
        this.name = name;
        this.roles = roles;
        this.labels = labels;
        this.annotations = annotations;
        this.creationTimestamp = creationTimestamp;
        this.taints = taints;
        this.unSchedulable = unSchedulable;
        this.conditions = conditions;
        this.addresses = addresses;
        this.capacity = capacity;
        this.allocatable = allocatable;
        this.systemInfo = systemInfo;
        this.podCIDR = podCIDR;
        this.podCIDRs = podCIDRs;
        this.pods = pods;
        this.allocateResources = allocateResources;
        this.events = events;
    }

    String name;
    String roles;
    Map<String, String> labels;
    Map<String, String> annotations;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;
    Map<String, String> taints;
    boolean unSchedulable;
    List<V1NodeCondition> conditions;
    List<V1NodeAddress> addresses;
    Map<String, Quantity> capacity;
    Map<String, Quantity> allocatable;
    V1NodeSystemInfo systemInfo;
    String podCIDR;
    List<String> podCIDRs;
    List<ObjectUsageResource> pods;
    ObjectUsageResource allocateResources;
    List<EventTable> events;

    @JsonIgnore
    public Quantity getAllocatableMetric(String metricName) {
        return allocatable.get(metricName);
    }

    public BigDecimal getActivationRatio(String metricName) {
        Quantity allocatableMetric = getAllocatableMetric(metricName);
        Quantity requestMetric = allocateResources.getRequests().get(metricName);

        if (StringUtils.equalsIgnoreCase("pods", metricName)) {
            return new BigDecimal(pods.size()).divide(allocatableMetric.getNumber(), 3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        }

        if (requestMetric == null) {
            return new BigDecimal(0);
        }

        return requestMetric.getNumber()
            .divide(allocatableMetric.getNumber(), 3, RoundingMode.HALF_UP)
            .multiply(new BigDecimal(100));
    }
}
