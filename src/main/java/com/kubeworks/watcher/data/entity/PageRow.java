package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kubeworks.watcher.data.converter.PageRowTypeConverter;
import com.kubeworks.watcher.data.vo.PageRowType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageRow extends BaseEntity {

//    page_row_id bigint unsigned auto_increment
//    primary key,
//    title       varchar(200)                          not null,
//    sort_order  bigint unsigned                       not null,
//    row_type    varchar(10)                           not null,
//    create_time timestamp   default CURRENT_TIMESTAMP not null,
//    update_time timestamp   default CURRENT_TIMESTAMP not null,
//    page_id     bigint unsigned                       null,
//    constraint PAGE_ROW_FK01

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_row_id", columnDefinition = "bigint unsigned", nullable = false)
    long pageRowId;

    @Column(name = "title", length = 200, nullable = false)
    String title;

    @Column(name = "sort_order", columnDefinition = "bigint unsigned", nullable = false)
    long sortOrder;

    @Column(name = "row_type", length = 10, nullable = false)
    //@ColumnDefault("'P'")
    @Convert(converter = PageRowTypeConverter.class)
    PageRowType rowType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "page_id", foreignKey = @ForeignKey(name = "PAGE_ROW_FK01"))
    Page page;

    @OneToMany(targetEntity = PageRowPanel.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pageRow")
    @OrderBy("sort_order ASC")
    List<PageRowPanel> pageRowPanels;
}
