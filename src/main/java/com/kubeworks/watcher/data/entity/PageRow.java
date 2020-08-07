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
public class PageRow extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned", nullable = false)
    long pageRowId;

//    @Column(columnDefinition = "bigint unsigned", nullable = false)
//    long pageId;

    @Column(columnDefinition = "tinyint(1) default 0", nullable = false)
    boolean divider;

    @Column(length = 200, nullable = false)
    String title;

    @Column(columnDefinition = "bigint unsigned", nullable = false)
    long sort;

    @OneToMany(targetEntity = PageRowPanel.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "page_row_id", foreignKey = @ForeignKey(name = "PAGE_ROW_FK01"))
    @OrderBy("sort ASC")
    List<PageRowPanel> panels;

}
