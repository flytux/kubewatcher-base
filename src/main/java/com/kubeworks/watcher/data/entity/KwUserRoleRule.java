package com.kubeworks.watcher.data.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(value=AuditingEntityListener.class)
public class KwUserRoleRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="rule_id", columnDefinition="bigint unsigned", nullable=false)
    long ruleId;

    @Column(name="rule_name", length=200, nullable=false)
    String rulename;

    @Column(name="rule", length=100, nullable=false)
    String rule;

    @Column(name="description", length=200, nullable=false)
    String description;

    @Column(name="auth_code", length=3)
    String authcode;
}
