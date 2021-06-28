package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.NamespaceSettable;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ObjectColumnDefinition;
import io.kubernetes.client.common.KubernetesObject;
import io.kubernetes.client.openapi.models.V1ListMeta;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

@Getter @Setter
public abstract class V1ObjectTableList<T extends NamespaceSettable, O extends KubernetesObject> {

    @SerializedName("apiVersion") private String apiVersion;
    @SerializedName("kind") private String kind;
    @SerializedName("metadata") private V1ListMeta metadata;
    @SerializedName("rows") private List<V1ObjectAsTable<O>> rows;
    @SerializedName("columnDefinitions") List<V1ObjectColumnDefinition> columnDefinitions;

    public void setRows(final List<V1ObjectAsTable<O>> rows) {
        this.rows = Objects.nonNull(rows) ? rows : ImmutableList.of();
    }

    public void setColumnDefinitions(final List<V1ObjectColumnDefinition> columnDefinitions) {
        this.columnDefinitions = Objects.nonNull(columnDefinitions) ? columnDefinitions : ImmutableList.of();
    }

    protected abstract T createInstance();

    protected abstract void putValueIntoField(final T builder, final String field, final String value);

    protected abstract void executeExtraProcess(final T data, final V1ObjectAsTable<O> row);

    public List<T> createDataTableList() {

        if (Objects.isNull(rows) || Objects.isNull(columnDefinitions)) { return ImmutableList.of(); }

        final List<T> res = Lists.newArrayListWithExpectedSize(rows.size());

        rows.forEach(e -> {
            final T data = createInstance();

            if (!CollectionUtils.isEmpty(e.getCells())) {
                IntStream.range(0, e.getCells().size()).forEach(n ->
                    putValueIntoField(data, columnDefinitions.get(n).getName().toLowerCase(Locale.getDefault()), e.getCells().get(n)));
            }

            if (Objects.nonNull(e.getObject().getMetadata())) {
                data.setNamespace(e.getObject().getMetadata().getNamespace());
            }

            executeExtraProcess(data, e);

            res.add(data);
        });

        return res;
    }
}
