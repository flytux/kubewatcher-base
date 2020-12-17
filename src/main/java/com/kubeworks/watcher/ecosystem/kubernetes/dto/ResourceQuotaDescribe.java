package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

import java.util.HashMap;
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
        Map <String, Resource> usedHard = new HashMap<>();
        for (String key : hard.keySet()) {
            Resource resource = new Resource(hard.get(key));
            resource.setUsed(used.get(key));
            usedHard.put(key, resource);
        }
        return usedHard;
    }

    @Getter
    @Setter
    public static class Resource {
        public Resource(Quantity hard) { this.hard = hard; }
        Quantity hard;
        Quantity used;
    }

    }
