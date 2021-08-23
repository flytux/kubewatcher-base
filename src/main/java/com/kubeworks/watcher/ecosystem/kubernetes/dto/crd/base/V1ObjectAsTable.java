package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base;

import com.google.gson.annotations.SerializedName;
import io.kubernetes.client.common.KubernetesObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class V1ObjectAsTable<O extends KubernetesObject> {

    @SerializedName("cells") private List<String> cells;
    @SerializedName("object") private O object;
}
