package com.zdd.login.dao;

import com.zdd.pojo.entity.MenuInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuDao extends JpaRepository<MenuInfo,Long> {

    /**
     * 获取角色的菜单信息
     * @return
     */
    @Query(value = "select bm.* from base_role_menu brm INNER JOIN base_menu bm ON brm.menuId=bm.id where brm.roleId=?1 and bm.leval=?2 and bm.parentId=?3 ",nativeQuery = true)
    public List<MenuInfo> getFirstMenuInfo(Long roleId, Integer leval,Long parentId);
}
