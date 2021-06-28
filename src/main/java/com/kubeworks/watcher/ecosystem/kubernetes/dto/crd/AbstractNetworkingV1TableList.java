package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NamespaceSettable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base.V1ObjectTableList;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter @Setter
public abstract class AbstractNetworkingV1TableList<T extends NamespaceSettable, O extends KubernetesObject> extends V1ObjectTableList<T, O> {

    @SerializedName("items") private List<O> items;

    protected abstract void executeExtraProcessWithMeta(final T data, final O e, final V1ObjectMeta meta);

    @Override
    public List<T> createDataTableList() {

        final List<O> source = getItems();
        if (Objects.isNull(source)) { return ImmutableList.of(); }

        final List<T> res = Lists.newArrayListWithExpectedSize(source.size());

        source.forEach(e -> {
            final T data = createInstance();

            if (Objects.nonNull(e.getMetadata())) { data.setNamespace(e.getMetadata().getNamespace()); }

            executeExtraProcessWithMeta(data, e, e.getMetadata());

            res.add(data);
        });

        return res;
    }
}
