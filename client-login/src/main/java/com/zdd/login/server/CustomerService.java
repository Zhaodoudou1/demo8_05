package com.zdd.login.server;

import com.zdd.login.config.HttpUtils;
import com.zdd.login.dao.MenuDao;
import com.zdd.login.dao.RoleDao;
import com.zdd.login.dao.UserDao;
import com.zdd.pojo.entity.MenuInfo;
import com.zdd.pojo.entity.RoleInfo;
import com.zdd.pojo.entity.UserInfo;
import org.apache.catalina.User;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CustomerService {
    @Autowired
    MenuDao menuDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;
    /**
     * 递归查询    左侧菜单
     * @param
     * @return
     */
    public UserInfo getUserByLogin(String loginName){
        System.out.println(loginName+"loginName");
        //获取用户信息
        UserInfo byLoginName = userDao.findByLoginName(loginName);
        System.out.println(byLoginName.getId()+"---------");
        System.out.println(byLoginName+"byLoginName");
        if(byLoginName!=null){
            //获取用户的角色信息
            RoleInfo roleInfoByUserId = roleDao.forRoleInfoByUserId(byLoginName.getId());
            //设置用户的角色信息
            byLoginName.setRoleInfo(roleInfoByUserId);

            if(roleInfoByUserId!=null){
                //获取用户的权限信息
                List<MenuInfo> firstMenuInfo = menuDao.getFirstMenuInfo(roleInfoByUserId.getId(), 1,0L);
                System.out.println("firstMenuInfo"+firstMenuInfo);
                //递归的查询子菜单权限
                Map<String,String> authMap=new Hashtable<>();
                this.getForMenuInfo(firstMenuInfo,roleInfoByUserId.getId(),authMap);
                //设置菜单的子权限
                byLoginName.setAuthmap(authMap);
                byLoginName.setListMenuInfo(firstMenuInfo);
            }
        }
        return byLoginName;
    }

    /**
     * 获取子权限的递归方法
     * @param firstMenuInfo
     * @param roleId
     */
    public void getForMenuInfo(List<MenuInfo> firstMenuInfo,Long roleId,Map<String,String> authMap){

        for(MenuInfo menuInfo:firstMenuInfo){
            int leval=menuInfo.getLeval() + 1;
            //获取下级的菜单信息
            List<MenuInfo> firstMenuInfo1 = menuDao.getFirstMenuInfo(roleId, leval,menuInfo.getId());
            if(firstMenuInfo1!=null){

                //整理后台的数据访问链接
                if(leval==4){
                    for(MenuInfo menu:firstMenuInfo1){
                        System.out.println(menu);
                        authMap.put(menu.getUrl(),"");
                    }
                }

                //设置查出来的菜单到父级对象中
                menuInfo.setMenuInfoList(firstMenuInfo1);
                //根据查出来的下级菜单继续查询该菜单包含的子菜单
                getForMenuInfo(firstMenuInfo1,roleId,authMap);
            }else{
                break;
            }
        }

    }

    public UserInfo selectUserName(String loginName) {

        return userDao.findByLoginName(loginName);
    }


    public String getAuthcode(String tel) {
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "80f9128752cf4e40a7a773c6f07bdfbc";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", tel);
        String randomString = this.getRandomString();
        querys.put("param", "code:"+randomString);
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            /* System.out.println("response.toString:"+response.toString());*/
            //获取response的body
            /*System.out.println("EntityUtils.toString:"+EntityUtils.toString(response.getEntity()));*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return randomString;
    }

    public String getRandomString(){
        Random random = new Random();
        String result="";
        for (int i=0;i<6;i++)
        {
            result+=random.nextInt(10);
        }

        return result;
    }

    public UserInfo selPhone(String tel) {

        return userDao.selPhone(tel);
    }
}
