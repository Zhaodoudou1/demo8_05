package com.zdd.business.controller;

import com.github.pagehelper.PageInfo;
import com.zdd.common.util.MD5;
import com.zdd.common.util.UID;
import com.zdd.pojo.ResponseResult;
import com.zdd.pojo.entity.MenuInfo;
import com.zdd.pojo.entity.RoleInfo;
import com.zdd.pojo.entity.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.zdd.business.server.CustomerService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "这是一个管理/用户；权限；角色的控制层/")
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
    @PostMapping("selectAll")
    @ResponseBody
    @ApiOperation("这个一个分页查询用户的接口")
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
    @PostMapping("deleteOne")
    @ResponseBody
    @ApiOperation("这个一个用户的单个删除的接口")
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

    @PostMapping("imgUrl")
    @ApiOperation("文件上传的接口")
    public void imgUrl(@Param("file")MultipartFile file) throws IOException {
        this.url = null;
        System.out.println("进入图片方法");
        File file2 = new File("F:\\Photo\\" + file.getOriginalFilename());
        file.transferTo(file2);

        File file1 = new File(file.getOriginalFilename());
        //可自定义大小
        Thumbnails.of(file2).scale(0.25f).toFile(file2.getAbsolutePath());
        this.url = file2.getAbsolutePath().substring(9);
    }



    /**
     * 修改方法
     * 使用的jpa
     * @param user
     * @return
     */
    @PostMapping("updateOne")
    @ResponseBody
    @ApiOperation("这个一个用户的修改的接口")
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
    @PostMapping("insertOneUser")
    @ResponseBody
    @ApiOperation("这个一个用户的添加的接口")
    public ResponseResult insertOneUser(@RequestBody UserInfo user){
        ResponseResult responseResult = ResponseResult.getResponseResult();

        Long uuidOrder = Long.valueOf(UID.getUUIDOrder());
        user.setId(uuidOrder);
        String lcg = MD5.encryptPassword(user.getPassword(), "lcg");

        System.out.println("添加=》的yrl"+this.url);
        user.setImgUrl(this.url);
        System.out.println("添加的密码=》"+lcg);
        user.setPassword(lcg);
        System.out.println("添加=》"+ user);
        customerService.insertOneUser(user);
        responseResult.setCode(200);
        return responseResult;
    }


    /**
     *查询角色和权限
     */
    @PostMapping("selectAllRoleAndMenu")
    @ResponseBody
    @ApiOperation("这个一个分页查询角色和角色拥有权限的的接口")
    public PageInfo<RoleInfo> selectAllRoleAndMenu(@RequestBody Map<String,Object> map){

        PageInfo<RoleInfo> roleInfoPageInfo = customerService.selectAllRoleAndMenu(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        return  roleInfoPageInfo;
    }

    /**
     * 查询所有的角色
     */
    @PostMapping("selectAllRole")
    @ResponseBody
    @ApiOperation("这个一个查询角色的接口")
    public List<RoleInfo> selectAllRole(){
        List<RoleInfo> roleInfos = customerService.selectAllRole();
        return roleInfos;
    }


    /**
     * 用户绑定角色
     */
    @PostMapping("insertRoleAndUserCertont")
    @ResponseBody
    @ApiOperation("这个一个用户给绑定角色接口")
    public ResponseResult insertRoleAndUserCertont(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();


        customerService.insertRoleAndUserCertont(Long.parseLong (map.get("id").toString()),Long.parseLong(map.get("roleValue").toString()));
        responseResult.setCode(200);
        return responseResult;
    }

    /**
     * 删除一个角色
     */
    @PostMapping("deleteOneRole")
    @ResponseBody
    @ApiOperation("这个一个删除角色的接口/  其中如果这个角色绑定用户的话是不可以删除的")
    public ResponseResult deleteOneRole(@RequestBody Map<String,Object> map) {
        ResponseResult responseResult = ResponseResult.getResponseResult();
        Long id = Long.valueOf(map.get("deleteid").toString());
        System.out.println("删除id==="+id);

        //查询删除的角色  是否正在绑定着用户
        UserInfo roleInUser = customerService.findRoleInUser(id);
        if(roleInUser != null){
            responseResult.setCode(500);
            responseResult.setResult(roleInUser.getUserName());
        }else {
            customerService.deleteOneRole(id);
            customerService.deleteOneRoleCentre(id);
            responseResult.setCode(200);
        }
        return responseResult;
    }

    /**
     *  添加角色
     */
    @PostMapping("insertOneRole")
    @ResponseBody
    @ApiOperation("这个一个添加角色的接口")
    public ResponseResult insertOneRole(@RequestBody RoleInfo roleInfo){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        customerService.insertOneRole(roleInfo);
        responseResult.setCode(200);
        return  responseResult;
    }

    /**
     * 查询权限
     */
    @PostMapping("findMenu")
    @ResponseBody
    @ApiOperation("这个一个查询权限的接口")
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
    @PostMapping("insertRoleAndMenuAndCentre")
    @ResponseBody
    @ApiOperation("这是一个给角色绑定权限的接口")
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


    /**
     * 递归查询所有权限
     * @return
     */
    @PostMapping("selectAllMenu")
    @ResponseBody
    @ApiOperation("这是一个递归查询所有权限的接口")
    public List<MenuInfo> test(){
        List<MenuInfo> test = customerService.test();
        System.out.println("遍历功能3");
        test.forEach(t->{
            System.out.println(t);
        });
        return test;
    }

    /**
     * 添加权限
     * @param map
     * @return
     */
    @PostMapping("insertMenu")
    @ResponseBody
    @ApiOperation("这是一个添加权限的接口")
    public ResponseResult insertMenu(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        customerService.insertMenu(map.get("menuName").toString(),map.get("url").toString(),Integer.parseInt(map.get("leval").toString())+1,Integer.parseInt(map.get("id").toString()));
        responseResult.setCode(200);
        return responseResult;
    }

    /**
     * 修改权限
     * @param map
     * @return
     */
    @PostMapping("updateMenu")
    @ResponseBody
    @ApiOperation("这是一个修改权限的接口")
    public ResponseResult updateMenu(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        customerService.updateMenu(Long.parseLong(map.get("id").toString()),map.get("menuName").toString(),map.get("url").toString(),Integer.parseInt(map.get("leval").toString()));
        responseResult.setCode(200);
        return responseResult;
    }

    /**
     * 删除一个权限
     * @param map
     * @return
     */
    @PostMapping("deleteMenu")
    @ResponseBody
    @ApiOperation("这是一个删除权限的接口/      如果该权限被绑定角色的时候是无法删除的")
    public ResponseResult deleteMenu(@RequestBody Map<String,Object>map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        System.out.println("删除权限id"+Long.parseLong(map.get("id").toString()));
        RoleInfo menuInRole = customerService.findMenuInRole(Long.parseLong(map.get("id").toString()));
        System.out.println(menuInRole+"查询权限");
        if(menuInRole.getRoleName() != null){
            System.out.println("进入删除权限中--------------------------------------");
            responseResult.setCode(500);
            responseResult.setResult(menuInRole.getRoleName());
        }else {
            customerService.deleteMenu(Long.parseLong(map.get("id").toString()));
            customerService.deleteMenuCentre(Long.parseLong(map.get("id").toString()));
            responseResult.setCode(200);
        }
        return responseResult;
    }


}
