package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KwUserRole extends BaseEntity implements GrantedAuthority {

    @EmbeddedId
    KwUserRoleId rolename;

    @MapsId("username")
    @ManyToOne
    @JoinColumn(name = "username")
    KwUser kwUser;

    @Column(name = "description", length = 200, nullable = false)
    String description; //description

    @OneToOne
    @JoinColumn(name = "ruleId")
    private KwUserRoleRule rule;

    @Override
    public String getAuthority() {
        return rolename.getRolename();
    }
}
