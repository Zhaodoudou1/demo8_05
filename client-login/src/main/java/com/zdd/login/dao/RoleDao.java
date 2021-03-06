package com.zdd.login.dao;

import com.zdd.pojo.entity.RoleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleDao extends JpaRepository<RoleInfo,Long> {

    /**
     * 根据用户ID获取角色信息
     * @param
     * @return
     */
    @Query(value = "select br.* from base_user_role bur INNER JOIN base_role br ON bur.roleId=br.id where bur.userId=?1",nativeQuery = true)
    RoleInfo forRoleInfoByUserId(Long id);

}
