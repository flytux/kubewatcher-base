package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.Map;

@Getter @Setter
@FieldDefaults(level=AccessLevel.PRIVATE)
public class NodeMetrics implements KubernetesObject {

    V1ObjectMeta metadata = new V1ObjectMeta();
    String kind;
    String apiVersion;
    String timestamp;
    String window;
    Map<String, Quantity> usage = Collections.emptyMap();
}
