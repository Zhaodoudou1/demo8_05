package com.zdd.business.server;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.zdd.business.dao.UserMapperDao;
import com.zdd.pojo.entity.MenuInfo;
import com.zdd.pojo.entity.RoleInfo;
import com.zdd.pojo.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.math.BigInteger;
import java.util.List;

@Component
public class CustomerService {



    @Autowired
    private UserMapperDao userMapperDao;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 查询所有用户
     * @param pageNum
     * @param pageSize
     * @param likeUserName
     * @param start
     * @param end
     * @param sex
     * @return
     */
    public PageInfo<UserInfo> findAll(Integer pageNum, Integer pageSize, String likeUserName, String start, String end, String sex,String userid){
        PageHelper.startPage(pageNum,pageSize);

        System.out.println("page"+pageNum+"pageSize"+pageSize+"like="+likeUserName+"start="+start+"end="+end+"sex="+sex);
        List<UserInfo> all = userMapperDao.findAll(likeUserName,start,end,sex);;
        System.out.println("遍历查询");
        all.forEach(u->{
            System.out.println(u);
        });
        PageInfo<UserInfo> userInfoPageInfo = new PageInfo<>(all);

        return userInfoPageInfo;
    }

    public void deleteOne(Long id) {
        userMapperDao.deleteOne(id);
    }


    public void updateOne(UserInfo user){
        System.out.println("user的Server/update/方法");
        userMapperDao.updateOne(user);
    }


    public void insertOneUser(UserInfo user) {
        userMapperDao.insertOneUser(user);
    }

    public PageInfo<RoleInfo> selectAllRoleAndMenu(Integer pageNum, Integer  pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<RoleInfo> list = userMapperDao.selectAllRoleAndMenu();

        for (RoleInfo li: list) {
            List<RoleInfo> roleInfos = userMapperDao.selectAllRoleAndMenuMids(li.getId());
            for (RoleInfo  role:roleInfos) {
                if(role.getMids() != null){
                    li.setIds(role.getMids().split(","));
                }
            }
        }
        System.out.println("查询setIds[]");
        for (RoleInfo l: list) {
            System.out.println(l);
        }
        PageInfo<RoleInfo> roleInfoPageInfo = new PageInfo<RoleInfo>(list);
        return roleInfoPageInfo;
    }

        public List<RoleInfo> selectAllRole() {
            List<RoleInfo> roleInfos = userMapperDao.selectAllRole();

            return roleInfos;
        }

    public void insertRoleAndUserCertont(Long id, Long roleValue) {
        userMapperDao.insertRoleAndUserCertont(id,roleValue);
    }

    public void deleteOneRole(Long id) {
        userMapperDao.deleteOneRole(id);
    }

    public void insertOneRole(RoleInfo roleInfo) {
        userMapperDao.insertOneRole(roleInfo);
    }




    /**
     * 递归查询角色
     * @return
     */
    public List<MenuInfo> findMenu(Long roleids) {
        List<MenuInfo> menu =  userMapperDao.findMenuByRoleId(roleids,1);

        this.getOtherMenu(menu,roleids);
        return  menu;

    }

    public void getOtherMenu(List<MenuInfo> menu,Long roleids){
        for (MenuInfo  menuInfo: menu) {
            //@Param("leval") int i,@Param("parentid") Long id,@Param("") Long roleids
            List<MenuInfo> childMenu = userMapperDao.getfindChildMenuByRoleId(menuInfo.getLeval() + 1, menuInfo.getId(),roleids);

            menuInfo.setMenuInfoList(childMenu);

            if(childMenu.size() > 0){
                this.getOtherMenu(childMenu,roleids);
            }
        }
    }













    public void insertRoleAndMenuAndCentre(String[] mids, long id) {
        userMapperDao.insertRoleAndMenuAndCentre(mids,id);
    }

    public void deleteOneRoleCentre(Long id) {
        userMapperDao.deleteOneRoleCentre(id);
    }

    public void deleteRoleAndMenuAndCentre(long id) {
        userMapperDao.deleteRoleAndMenuAndCentre(id);
    }


    //权限管理的全查
    public List<MenuInfo> test() {
        List<MenuInfo> menu =  userMapperDao.findMenu(1);
        this.getOther(menu);
        return  menu;

    }

    public void getOther(List<MenuInfo> menu){
        for (MenuInfo  menuInfo: menu) {
            int leval = menuInfo.getLeval()+1;
            if(leval ==4){
                break;
            }
            List<MenuInfo> childMenu = userMapperDao.getChildMenu(leval, menuInfo.getId());
            menuInfo.setMenuInfoList(childMenu);
            if(childMenu != null){
                if(leval==4){
                  break;
                }
                getOther(childMenu);
            }
        }
    }

    public void insertMenu(String menuName, String url, int leval,int parentid) {
        userMapperDao.insertMenu(menuName,url,leval,parentid);
    }

    public void updateMenu(long id, String menuName, String url, int leval) {
        userMapperDao.updateMenu(id,menuName,url,leval);
    }

    public void deleteMenu(long id) {
        userMapperDao.deleteMenu(id);
    }

    public void deleteMenuCentre(long id) {
        userMapperDao.deleteMenuCentre(id);
    }


    public UserInfo findRoleInUser(Long id) {
        return userMapperDao.findRoleInUser(id);
    }

    public RoleInfo findMenuInRole(long id) {

        return userMapperDao.findMenuInRole(id);
    }

    public UserInfo findRoleByUserId(long userid) {

        return userMapperDao.findRoleByUserId(userid);
    }

    public void insertOneRole(String roleName, String miaoShu, int leval, long roleids) {
        userMapperDao.insertOneRole(roleName,miaoShu,leval,roleids);
    }

    public UserInfo findUserByLoginName(String username) {
        return userMapperDao.findUserByLoginName(username);
    }

    public UserInfo findUserByEmail(String email) {
        return  userMapperDao.findUserByEmail(email);
    }

    public void updatePasswordByLoginName(String username, String password) {
        userMapperDao.updatePasswordByLoginName(username,password);
    }
}
