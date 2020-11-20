package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageRowPanel extends BaseEntity {

//    panel_id       bigint unsigned auto_increment            primary key,
//    title          varchar(200)                              not null,
//    sort_order     bigint unsigned                           not null,
//    panel_type     varchar(20)                               not null,
//    fragment_name  varchar(50)                                   null,
//    chart_type     varchar(20)                               not null,
//    refresh_interval  bigint unsigned       default 60000    not null,
//    yaxis_label    varchar(50)                               not null,
//    yaxis_unit     varchar(20)                               not null,
//    yaxis_min      varchar(10)                               not null,
//    yaxis_max      varchar(10)                               not null,
//    xaxis_mode     varchar(20)                               not null,
//    create_time    timestamp       default CURRENT_TIMESTAMP not null,
//    update_time    timestamp       default CURRENT_TIMESTAMP not null,
//    page_row_id bigint unsigned                           null,
//    constraint PAGE_ROW_PANEL_FK01

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "panel_id", columnDefinition = "bigint unsigned", nullable = false)
    long panelId;

    @Column(name = "title", length = 200, nullable = false)
    String title;

    @Column(name = "sort_order", columnDefinition = "bigint unsigned default 0", nullable = false)
    long sortOrder;

    @Column(name = "panel_type", length = 20, nullable = false)
    String panelType; //TODO enum으로 변경

    @Column(name = "fragment_name", length = 50, nullable = true)
    String fragmentName;

    @Column(name = "chart_type", length = 20, nullable = false)
    String chartType;

    @Column(name = "main_yn", length = 1, nullable = false)
    String mainYn;

    @Column(name = "refresh_interval", columnDefinition = "bigint unsigned default 60000", nullable = false)
    int refreshIntervalMillis;

    @Column(name = "yaxis_label", length = 50, nullable = false)
    String yaxisLabel;

    @Column(name = "yaxis_unit", length = 20, nullable = false)
    String yaxisUnit;

    @Column(name = "yaxis_min", length = 10, nullable = false)
    String yaxisMin;

    @Column(name = "yaxis_max", length = 10, nullable = false)
    String yaxisMax;

    @Column(name = "xaxis_mode", length = 20, nullable = false)
    String xaxisMode;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "page_row_id", foreignKey = @ForeignKey(name = "PAGE_ROW_PANEL_FK01"))
    PageRow pageRow;

    @OneToMany(targetEntity = ChartQuery.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pageRowPanel")
    List<ChartQuery> chartQueries;
}
