package com.zdd.business.dao;

import com.zdd.pojo.entity.MenuInfo;
import com.zdd.pojo.entity.RoleInfo;
import com.zdd.pojo.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapperDao {


    public List<UserInfo> findAll(@Param("likeUserName") String likeUserName, @Param("start")  String start, @Param("end")  String end, @Param("sex") String sex);

    void deleteOne(@Param("id") Long id);

    void updateOne(@Param("user") UserInfo user);

    void insertOneUser(@Param("user") UserInfo user);

    List<RoleInfo> selectAllRoleAndMenu();

    List<RoleInfo> selectAllRole();

    void insertRoleAndUserCertont(@Param("id") Long id,@Param("roleValue") Long roleValue);

    void deleteOneRole(@Param("id") Long id);

    void insertOneRole(@Param("role") RoleInfo roleInfo);

    List<MenuInfo> findMenu(@Param("leave") Integer leave);

    List<MenuInfo> getChildMenu(@Param("leave") int i,@Param("parentid") Long id);

    void insertRoleAndMenuAndCentre(@Param("mids") String[] mids,@Param("id") long id);

    void deleteOneRoleCentre(@Param("id") Long id);

    List<RoleInfo> selectAllRoleAndMenuMids(@Param("id") Long id);

    void deleteRoleAndMenuAndCentre(@Param("id") long id);

    void insertMenu(@Param("menuName") String menuName, @Param("url") String url, @Param("leval") int leval, @Param("parentid") int parentid);

    void updateMenu(@Param("id") long id, @Param("menuName") String menuName, @Param("url") String url, @Param("leval")int leval);

    void deleteMenu(@Param("id") long id);

    void deleteMenuCentre(@Param("id")long id);

    UserInfo findRoleInUser(@Param("id") Long id);

    RoleInfo findMenuInRole(@Param("id") long id);

    UserInfo findRoleByUserId(@Param("id") long userid);

    void insertOneRole(@Param("roleName") String roleName,@Param("miaoShu") String miaoShu,@Param("leval") int leval,@Param("parentid") long roleids);

    List<MenuInfo> findMenuByRoleId(@Param("roleid")Long roleids,@Param("leval") int i);

    List<MenuInfo> getfindChildMenuByRoleId(@Param("leval") int i,@Param("parentid") Long id,@Param("roleid") Long roleids);

    UserInfo findUserByLoginName(@Param("loginName") String username);

    UserInfo findUserByEmail(@Param("email") String email);

    void updatePasswordByLoginName(@Param("loginName") String username,@Param("password") String password);
}
