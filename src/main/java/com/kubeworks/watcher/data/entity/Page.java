package com.kubeworks.watcher.data.entity;

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
public class Page extends BaseEntity {

//    page_id     bigint unsigned auto_increment
//    primary key,
//    title       varchar(200)                          not null,
//    create_time timestamp default CURRENT_TIMESTAMP not null,
//    update_time timestamp default CURRENT_TIMESTAMP not null,
//    description varchar(500)                        null,
//    menu_id     bigint unsigned                     not null

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "page_id", columnDefinition = "bigint unsigned", nullable = false)
    long pageId;

    @Column(name = "title", length = 200, nullable = false)
    String title;

    @Column(name = "description", length = 500)
    String description;

    @Column(name = "menu_id", columnDefinition = "bigint unsigned", nullable = false)
    long menuId;

    @OneToMany(targetEntity = PageRow.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "page")
    @OrderBy("sort_order ASC")
    List<PageRow> rows;

    @OneToMany(targetEntity = PageVariable.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "page")
    @OrderBy("sort_order ASC")
    List<PageVariable> variables;

}
