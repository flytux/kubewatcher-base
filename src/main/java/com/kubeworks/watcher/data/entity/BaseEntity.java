package com.kubeworks.watcher.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
public class BaseEntity {

    @CreatedDate
    @Column(columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", nullable = false)
    private LocalDateTime updateTime;

}
