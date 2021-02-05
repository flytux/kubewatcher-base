package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.KwUserRole;
import com.kubeworks.watcher.data.entity.KwUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KwUserRoleRepository extends JpaRepository<KwUserRole, KwUserRoleId> {
    List<KwUserRole> findAllBy();

    @Query(value="SELECT DISTINCT kw.rolename FROM Kw_User_Role kw",nativeQuery = true)
    List<String> findDistinctAllBy();

//    @Query(value="SELECT DISTINCT kw.rolename FROM Kw_User_Role kw",nativeQuery = true)
//    int deleteById(KwUserRoleId kwUserRoleId);

    //@Query(value="DELETE FROM Kw_User_Role kw WHERE ",nativeQuery = true)
    //int deleteId(String username, String roleId  );
}