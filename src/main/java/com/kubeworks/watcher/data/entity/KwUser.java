package com.kubeworks.watcher.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if (type.equals("modify")){
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

    public String getUserRole() {
        List<String> rolenameList = new ArrayList<>();
        KwUserRoleId roleId = new KwUserRoleId();

        for (int i=0; i< role.size(); i++) {
            roleId = role.get(i).getRolename();
            String rolename = roleId.getRolename();
            rolenameList.add(rolename);
        }

        String roles = "";

        if (CollectionUtils.isNotEmpty(rolenameList)) {
            roles = rolenameList.stream().collect(Collectors.joining(", "));
        } else {
            roles = "-";
        }

        return roles;
    }

}