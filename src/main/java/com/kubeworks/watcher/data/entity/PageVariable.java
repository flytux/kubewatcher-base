package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kubeworks.watcher.data.converter.QueryTypeConverter;
import com.kubeworks.watcher.data.converter.VariableTypeConverter;
import com.kubeworks.watcher.data.vo.QueryType;
import com.kubeworks.watcher.data.vo.VariableType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
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


//    variable_id bigint unsigned auto_increment                    primary key,
//    name                varchar(100)                              not null,
//    sort_order          bigint unsigned                           not null,
//    edge_fields         varchar (100)                             null,
//    query_type          varchar(20)                               not null,
//    variable_type       varchar(20)                               not null,
//    refresh_interval    varchar(3)                                not null,
//    hidden_yn           varchar(1)                                not null,
//    job_name            varchar(50)                               not null,
//    api_query           varchar(1000)                             not null,
//    create_time     timestamp           default CURRENT_TIMESTAMP not null,
//    update_time     timestamp           default CURRENT_TIMESTAMP not null,
//    page_id     bigint unsigned                                   null,
//    constraint PAGE_VARIABLE_FK01


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variable_id", columnDefinition = "bigint unsigned", nullable = false)
    long variableId;

    @Column(name = "name", length = 100, nullable = false)
    String name;

    @Column(name = "sort_order", columnDefinition = "bigint unsigned default 0", nullable = false)
    long sortOrder;

    @Column(name = "edge_fields", length = 100)
    String edgeFields;

    @Column(name = "query_type", length = 20, nullable = false)
    @Convert(converter = QueryTypeConverter.class)
    QueryType queryType;

    @Column(name = "variable_type", length = 20, nullable = false)
    @Convert(converter = VariableTypeConverter.class)
    VariableType variableType;

    @Column(name = "refresh_interval", length = 3, nullable = false)
    String refreshInterval;

    @Column(name = "hidden_yn", length = 1, nullable = false)
    String hiddenYn;

    @Column(name = "job_name", length = 50, nullable = false)
    String jobName;

    @Column(name = "api_query", length = 1000, nullable = false)
    String apiQuery;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "page_id", foreignKey = @ForeignKey(name = "PAGE_VARIABLE_FK01"))
    Page page;

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

