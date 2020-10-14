package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.base;

import com.google.gson.annotations.SerializedName;
import com.kubeworks.watcher.ecosystem.kubernetes.dto.crd.V1ObjectColumnDefinition;
import io.kubernetes.client.openapi.models.V1ListMeta;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public abstract class V1ObjectTableList<T, O> {

    public static final String SERIALIZED_NAME_API_VERSION = "apiVersion";
    @SerializedName(SERIALIZED_NAME_API_VERSION)
    private String apiVersion;

    public static final String SERIALIZED_NAME_KIND = "kind";
    @SerializedName(SERIALIZED_NAME_KIND)
    private String kind;

    public static final String SERIALIZED_NAME_METADATA = "metadata";
    @SerializedName(SERIALIZED_NAME_METADATA)
    private V1ListMeta metadata;

    public static final String SERIALIZED_NAME_ITEMS = "rows";
    @SerializedName(SERIALIZED_NAME_ITEMS)
    private List<V1ObjectAsTable<O>> rows;

    public static final String SERIALIZED_COLUMN_DEFINITIONS = "columnDefinitions";
    @SerializedName(SERIALIZED_COLUMN_DEFINITIONS)
    List<V1ObjectColumnDefinition> columnDefinitions;

    public void setRows(List<V1ObjectAsTable<O>> rows) {
        if (rows == null) {
            this.rows = Collections.emptyList();
        }
        this.rows = rows;
    }

    public void setColumnDefinitions(List<V1ObjectColumnDefinition> columnDefinitions) {
        if (columnDefinitions == null) {
            this.columnDefinitions = Collections.emptyList();
        }
        this.columnDefinitions = columnDefinitions;
    }

    protected abstract T getDataObject();

    protected abstract void makeObject(T builder, String fieldName, String value);


    public List<T> getDataTable() {
        if (rows == null || columnDefinitions == null) {
            return Collections.emptyList();
        }

        List<T> list = new ArrayList<>(rows.size());
        for (V1ObjectAsTable<O> row : rows) {
            T data = getDataObject();
            final List<String> cells = row.getCells();
            IntStream.range(0, cells.size()).forEach(index -> {
                String value = cells.get(index);
                V1ObjectColumnDefinition columnDefinition = columnDefinitions.get(index);
                String fieldName = columnDefinition.getName().toLowerCase();
                makeObject(data, fieldName, value);
            });
            list.add(data);
        }
        return list;
    }


}
