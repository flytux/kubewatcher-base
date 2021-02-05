package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KwUser extends BaseEntity {

    @Id
    @Column(name = "username", length = 20, nullable = false)
    String username; //user id

    @Column(name = "password", length = 200, nullable = false)
    String password; //user password

    @Column(name = "name", length = 20, nullable = false)
    String name; //성명

    @Column(name = "userno", length = 7, nullable = false)
    String userno; //사번

    @Column(name = "dept", length = 200, nullable = false)
    String dept; //부서명

    // Fetch type  'Lazy' 설정하면, 로그인시 유저에 속해있는 롤 가져올때 'LazyInitializationException' 에러 발생함
    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "kwUser")
    List<KwUserRole> role;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "groupname", foreignKey = @ForeignKey(name = "KW_USER_FK01"))
    KwUserGroup kwUserGroup;
}