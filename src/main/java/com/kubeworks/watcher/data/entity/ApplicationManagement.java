package com.kubeworks.watcher.data.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name="application_management")
@FieldDefaults(level=AccessLevel.PRIVATE)
@EntityListeners(value=AuditingEntityListener.class)
public class ApplicationManagement extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="application_id", columnDefinition="bigint unsigned", nullable=false)
    long applicationId;

    @Column(name="code", length=50, nullable=false)
    String code; // code

    @Column(name="name", length=50, nullable=false)
    String name; // name

    @Column(name="namespace", length=50, nullable=false)
    String namespace; // namespace

    @Column(name="display_name", length=50, nullable=false)
    String displayName; // displayName
}
