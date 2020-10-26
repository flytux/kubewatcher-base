package com.kubeworks.watcher.ecosystem.kubernetes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.kubernetes.client.custom.Quantity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.joda.time.DateTime;

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

}
