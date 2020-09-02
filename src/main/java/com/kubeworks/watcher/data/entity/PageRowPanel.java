package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class PageRowPanel extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned", nullable = false)
    long panelId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "page_row_id", foreignKey = @ForeignKey(name = "PAGE_ROW_PANEL_FK01"))
    PageRow pageRow;

    @Column(length = 200, nullable = false)
    String title;

    @Column(length = 20, nullable = false)
    String panelType; //TODO enum으로 변경

    @Column(length = 20, nullable = false)
    String dataType; // TODO enum으로 변경

    @Column(length = 1000, nullable = false)
    String src;

    @Column(columnDefinition = "bigint unsigned default 0", nullable = false)
    long sort;



}
