package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kubeworks.watcher.data.converter.PageRowTypeConverter;
import com.kubeworks.watcher.data.vo.PageRowType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageRow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint unsigned", nullable = false)
    long pageRowId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "page_id", foreignKey = @ForeignKey(name = "PAGE_ROW_FK01"))
    Page page;

    @Column(length = 10, nullable = false)
    @ColumnDefault("'P'")
    @Convert(converter = PageRowTypeConverter.class)
    PageRowType type;

    @Column(length = 200, nullable = false)
    String title;

    @Column(columnDefinition = "bigint unsigned", nullable = false)
    long sort;

    @OneToMany(targetEntity = PageRowPanel.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pageRow")
    @OrderBy("sort ASC")
    List<PageRowPanel> panels;

}
