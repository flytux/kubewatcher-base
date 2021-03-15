package com.kubeworks.watcher.data.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KwUserRole extends BaseEntity {

    @EmbeddedId
    KwUserRoleId rolename;

    @MapsId("username")
    @ManyToOne
    @JoinColumn(name = "username")
    KwUser kwUser;

    @OneToOne
    @JoinColumn(name = "ruleId")
    private KwUserRoleRule rule;
}
