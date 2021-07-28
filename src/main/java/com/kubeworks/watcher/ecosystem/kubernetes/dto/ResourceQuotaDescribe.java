package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceQuotaDescribe {

    @Builder
    private ResourceQuotaDescribe(String name, String namespace, String uid, DateTime creationTimestamp,
                                  Map<String, String> labels, Map<String, String> annotations,
                                  Map<String, Quantity> hard, Map<String, Quantity> used,
                                  List<String> scopes, Map<String, String> scopeSelector) {
        this.name = name;
        this.namespace = namespace;
        this.uid = uid;
        this.creationTimestamp = creationTimestamp;
        this.labels = labels;
        this.annotations = annotations;
        this.hard = hard;
        this.used = used;
        this.scopes = scopes;
        this.scopeSelector = scopeSelector;
    }

    String name;
    String namespace;
    String uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    DateTime creationTimestamp;

    Map<String, String> labels;
    Map<String, String> annotations;

    Map<String, Quantity> hard;
    Map<String, Quantity> used;

    List<String> scopes;
    Map<String, String> scopeSelector;

    public Map<String, Resource> getUsedHard() {

        if (CollectionUtils.isEmpty(hard)) { return ImmutableMap.of(); }

        final Map <String, Resource> usedHard = Maps.newHashMapWithExpectedSize(hard.size());
        for (final Map.Entry<String, Quantity> e : hard.entrySet()) {
            usedHard.put(e.getKey(), new Resource(e.getValue(), used.get(e.getKey())));
        }

        return usedHard;
    }

    @Getter
    @Setter
    public static class Resource {

        public Resource(Quantity hard, Quantity used) {
            this.hard = hard; this.used = used;
        }

        private Quantity hard;
        private Quantity used;
    }
}
