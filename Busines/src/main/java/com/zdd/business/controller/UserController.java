package com.zdd.business.controller;

import com.github.pagehelper.PageInfo;
import com.zdd.common.util.UID;
import com.zdd.pojo.ResponseResult;
import com.zdd.pojo.entity.MenuInfo;
import com.zdd.pojo.entity.RoleInfo;
import com.zdd.pojo.entity.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.zdd.business.server.CustomerService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    CustomerService customerService;

    public String url;

    /**
     * 全部查询
     *
     * @param
     * @param
     * @return
     */
    @RequestMapping("selectAll")
    @ResponseBody
    public PageInfo<UserInfo> selectAll(@RequestBody Map<String,Object> map) {
        System.out.println(" map.get(\"pageNum\")"+ map.get("pageNum"));
        PageInfo<UserInfo> all = customerService.findAll(Integer.parseInt( map.get("pageNum").toString()),Integer.parseInt( map.get("pageSize").toString()), map.get("likeUserName").toString(),map.get("start").toString(),map.get("end").toString(),map.get("sex").toString());

        return all;
    }

    /**
     * 用户的单个删除
     * @param
     * @return
     */
    @RequestMapping("deleteOne")
    @ResponseBody
    public ResponseResult deleteOne(@RequestBody Map<String,Object> map) {
        Long id = Long.valueOf(map.get("deleteid").toString());
        System.out.println(id+"id");
        System.out.println("删除id==="+id);
        ResponseResult responseResult = ResponseResult.getResponseResult();

        customerService.deleteOne(id);
        responseResult.setCode(200);
        return responseResult;
    }


    /**
     * 文件上传
     */
    @RequestMapping("imgUrl")
    public void imgUrl(@Param("file")MultipartFile file) throws IOException {
        System.out.println("进入图片方法");
        file.transferTo(new File("F:\\Photo\\"+file.getOriginalFilename()));
        this.url = file.getOriginalFilename();
        System.out.println(url);
    }

    /**
     * 修改方法
     * 使用的jpa
     * @param user
     * @return
     */
    @RequestMapping("updateOne")
    @ResponseBody
    public ResponseResult updateOne(@RequestBody UserInfo user){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        user.setImgUrl(url);
        System.out.println("修改方法==》"+user);
        System.out.println("user=======>"+user.getId());
        customerService.updateOne(user);
        responseResult.setCode(200);
        return responseResult;
    }

    /**
     * 添加
     * @param user
     * @return
     */
    @RequestMapping("insertOneUser")
    @ResponseBody
    public ResponseResult insertOneUser(@RequestBody UserInfo user){
        ResponseResult responseResult = ResponseResult.getResponseResult();

        Long uuidOrder = Long.valueOf(UID.getUUIDOrder());
        user.setId(uuidOrder);

        System.out.println("添加=》的yrl"+this.url);
        user.setImgUrl(this.url);
        System.out.println("添加=》"+ user);
        customerService.insertOneUser(user);
        responseResult.setCode(200);
        return responseResult;
    }


    /**
     *查询角色和权限
     */
    @RequestMapping("selectAllRoleAndMenu")
    @ResponseBody
    public PageInfo<RoleInfo> selectAllRoleAndMenu(@RequestBody Map<String,Object> map){

        PageInfo<RoleInfo> roleInfoPageInfo = customerService.selectAllRoleAndMenu(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        return  roleInfoPageInfo;
    }

    /**
     * 查询所有的角色
     */
    @RequestMapping("selectAllRole")
    @ResponseBody
    public List<RoleInfo> selectAllRole(){
        List<RoleInfo> roleInfos = customerService.selectAllRole();
        return roleInfos;
    }


    /**
     * 用户绑定角色
     */
    @RequestMapping("insertRoleAndUserCertont")
    @ResponseBody
    public ResponseResult insertRoleAndUserCertont(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();


        customerService.insertRoleAndUserCertont(Long.parseLong (map.get("id").toString()),Long.parseLong(map.get("roleValue").toString()));
        responseResult.setCode(200);
        return responseResult;
    }

    /**
     * 删除一个角色
     */
    @RequestMapping("deleteOneRole")
    @ResponseBody
    public ResponseResult deleteOneRole(@RequestBody Map<String,Object> map) {
        ResponseResult responseResult = ResponseResult.getResponseResult();
        Long id = Long.valueOf(map.get("deleteid").toString());
        System.out.println(id+"id");
        System.out.println("删除id==="+id);
       customerService.deleteOneRole(id);
        customerService.deleteOneRoleCentre(id);
        responseResult.setCode(200);
        return responseResult;
    }

    /**
     *  添加角色
     */
    @RequestMapping("insertOneRole")
    @ResponseBody
    public ResponseResult insertOneRole(@RequestBody RoleInfo roleInfo){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        customerService.insertOneRole(roleInfo);
        responseResult.setCode(200);
        return  responseResult;
    }

    /**
     * 查询权限
     */
    @RequestMapping("findMenu")
    @ResponseBody
    public ResponseResult findMenu(){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        List<MenuInfo> menu = customerService.findMenu();

        responseResult.setResult(menu);
        return responseResult;
    }

    /**
     * 添加角色权限的绑定
     * 中间表添加
     */
    @RequestMapping("insertRoleAndMenuAndCentre")
    @ResponseBody
    public ResponseResult insertRoleAndMenuAndCentre(@RequestBody Map<String,Object>map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        System.out.println("mids==->"+map.get("mids").toString());
        String[] mids = map.get("mids").toString().split(",");
        long id = Long.parseLong(map.get("id").toString());
        customerService.deleteRoleAndMenuAndCentre(id);
        customerService.insertRoleAndMenuAndCentre(mids,id);
        responseResult.setCode(200);
        return responseResult;
    }

    @RequestMapping("selectAllMenu")
    @ResponseBody
    public List<MenuInfo> test(){
        List<MenuInfo> test = customerService.test();
        System.out.println("遍历功能3");
        test.forEach(t->{
            System.out.println(t);
        });
        return test;
    }

    @RequestMapping("insertMenu")
    @ResponseBody
    public ResponseResult insertMenu(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        customerService.insertMenu(map.get("menuName").toString(),map.get("url").toString(),Integer.parseInt(map.get("leval").toString())+1,Integer.parseInt(map.get("id").toString()));
        responseResult.setCode(200);
        return responseResult;
    }

    @RequestMapping("updateMenu")
    @ResponseBody
    public ResponseResult updateMenu(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        customerService.updateMenu(Long.parseLong(map.get("id").toString()),map.get("menuName").toString(),map.get("url").toString(),Integer.parseInt(map.get("leval").toString()));
        responseResult.setCode(200);
        return responseResult;
    }

    @RequestMapping("deleteMenu")
    @ResponseBody
    public ResponseResult deleteMenu(@RequestBody Map<String,Object>map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        System.out.println("删除方法"+Long.parseLong(map.get("id").toString()));
        customerService.deleteMenu(Long.parseLong(map.get("id").toString()));
        customerService.deleteMenuCentre(Long.parseLong(map.get("id").toString()));
        responseResult.setCode(200);
        return responseResult;
    }
}
