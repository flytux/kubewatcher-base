package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kubeworks.watcher.data.converter.VariableTypeConverter;
import com.kubeworks.watcher.data.vo.VariableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageVariable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned", nullable = false)
    long variableId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "page_id", foreignKey = @ForeignKey(name = "PAGE_VARIABLE_FK01"))
    Page page;

    @Column(length = 100, nullable = false)
    String name;

    @Column(length = 20, nullable = false)
    @ColumnDefault("'api'")
    @Convert(converter = VariableTypeConverter.class)
    VariableType type;

    @Column(length = 1000, nullable = false)
    String src;

    @Column(columnDefinition = "bigint unsigned default 0", nullable = false)
    long sort;

    @Column(length = 100)
    String edgeFields;

    @Transient
    List<?> values;

    @Transient
    List<?> refIds;

    public List<String> getEdgeFields() {
        if (edgeFields == null || edgeFields.isEmpty()) {
            return Collections.emptyList();
        }
        String[] split = edgeFields.split(",");
        return Arrays.asList(split);
    }
}

