package com.kubeworks.watcher.data.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Embeddable
@Getter
@Setter
public class KwUserRoleId implements Serializable {

    @EqualsAndHashCode.Include
    String username;

    @EqualsAndHashCode.Include
    @Column(name="rolename")
    String rolename;

}