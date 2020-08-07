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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned", nullable = false)
    long pageId;

    @Column(columnDefinition = "bigint unsigned", nullable = false)
    long menuId;

    @Column(length = 500)
    String description;

    @OneToMany(targetEntity = PageRow.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "page_id", foreignKey = @ForeignKey(name = "PAGE_FK01"))
    @OrderBy("sort ASC")
    List<PageRow> rows;

}
