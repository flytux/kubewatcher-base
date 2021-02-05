package com.kubeworks.watcher.data.entity;

import com.sun.istack.internal.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.checkerframework.common.value.qual.MinLen;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KwUserGroup extends BaseEntity {

    @Id
    @NotNull
    @MinLen(5)
    @Column(name = "groupname", length = 40, nullable = false)
    String groupname; //group id

    @NotNull
    @Column(name = "description", length = 200, nullable = false)
    String description; //description

    @OneToMany(targetEntity = KwUser.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "kwUserGroup")
    List<KwUser> users;
}
