package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
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
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "kwUser", orphanRemoval = true)
    List<KwUserRole> role;

    public void setRole(List<KwUserRole> role, String type) {
        if ("modify".equals(type)){
            this.role.clear();
            this.role.addAll(role);
        } else {
            this.role = role;
        }
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "groupname", foreignKey = @ForeignKey(name = "KW_USER_FK01"))
    KwUserGroup kwUserGroup;

    // TODO 코드수정 확인 필요
    public String getUserRole() {

        final List<String> rolenameList = new ArrayList<>();

        if (!role.isEmpty()) {
            role.forEach(r -> rolenameList.add(r.getRolename().getRolename()));
        }

        return CollectionUtils.isNotEmpty(rolenameList) ? String.join(", ", rolenameList) : "-";
    }
}
