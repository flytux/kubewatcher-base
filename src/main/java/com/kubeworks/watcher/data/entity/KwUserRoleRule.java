package com.kubeworks.watcher.data.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Size;

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

//    @Size(min = 5, message = "Role Name은 최소 5자리 입니다.")
//    @Length(min = 5, message = "Role Name은 최소 5자리 입니다.")
    @Column(name = "rule_name", length = 200, nullable = false)
    String rulename;

    @Column(name = "rule", length = 100, nullable = false)
    String rule;

    @Column(name = "description", length = 200, nullable = false)
    String description; //description

//    @OneToOne(mappedBy = "rule")
//    private KwUserRole userRole;
}