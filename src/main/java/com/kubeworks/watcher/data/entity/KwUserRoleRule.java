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
public class KwUserRoleRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id", columnDefinition = "bigint unsigned", nullable = false)
    long ruleId;

    @Column(name = "rule_name", length = 200, nullable = false)
    String rulename;

    @Column(name = "rule", length = 100, nullable = false)
    String rule;
//
//    @OneToOne(mappedBy = "rule")
//    private KwUserRole userRole;
}