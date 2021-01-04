package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kubeworks.watcher.data.converter.QueryTypeConverter;
import com.kubeworks.watcher.data.vo.QueryType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChartQuery extends BaseEntity {
//    c_query_id bigint unsigned auto_increment                    primary key,
//    query_type   varchar(1000)        default 'METRIC'           not null,
//    api_query   varchar(1000)                                    not null,
//    legend      varchar(100)                                     not null,
//    query_step  varchar(3)                                       not null,
//    create_time     timestamp          default CURRENT_TIMESTAMP not null,
//    update_time     timestamp          default CURRENT_TIMESTAMP not null,
//    panel_id    bigint unsigned                                  null,
//    constraint CHART_QUERY_FK01
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_query_id", columnDefinition = "bigint unsigned", nullable = false)
    long cQueryId;

    @Column(name = "query_type", columnDefinition = "VARCHAR(10) default 'METRIC'", length = 10, nullable = false)
    @Convert(converter = QueryTypeConverter.class)
    QueryType queryType;

    @Column(name = "api_query", length = 1000, nullable = false)
    String apiQuery;

    @Column(name = "legend", length = 100, nullable = false)
    String legend;

    @Column(name = "query_step", length = 3, nullable = false)
    String queryStep;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "panel_id", foreignKey = @ForeignKey(name = "CHART_QUERY_FK01"))
    PageRowPanel pageRowPanel;

}
