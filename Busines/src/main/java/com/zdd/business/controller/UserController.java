package com.zdd.business.controller;

import com.github.pagehelper.PageInfo;
import com.sun.deploy.net.HttpUtils;
import com.zdd.business.config.EmailUtil;
import com.zdd.common.util.MD5;
import com.zdd.common.util.UID;
import com.zdd.pojo.ResponseResult;
import com.zdd.pojo.entity.MenuInfo;
import com.zdd.pojo.entity.RoleInfo;
import com.zdd.pojo.entity.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.http.HttpResponse;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.zdd.business.server.CustomerService;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@Api(tags = "这是一个管理/用户；权限；角色的控制层/")
public class UserController {
    @Autowired
    CustomerService customerService;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

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
    @ApiOperation("这个一个分页查询用户的接口")
    public PageInfo<UserInfo> selectAll(@RequestBody Map<String,Object> map) {
        System.out.println(" map.get(\"pageNum\")"+ map.get("pageNum"));
        PageInfo<UserInfo> all = customerService.findAll(Integer.parseInt( map.get("pageNum").toString()),Integer.parseInt( map.get("pageSize").toString()), map.get("likeUserName").toString(),map.get("start").toString(),map.get("end").toString(),map.get("sex").toString(),map.get("userid").toString());

        return all;
    }

    /**
     * 用户的单个删除
     * @param
     * @return
     */
    @RequestMapping("deleteOne")
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

    @RequestMapping("imgUrl")
    @ApiOperation("文件上传的接口")
    public void imgUrl(@Param("file")MultipartFile file) throws IOException {

        System.out.println("进入图片方法");
        String imgUrl = "F:\\Photo\\" + file.getOriginalFilename();
        File file2 = new File(imgUrl);

        //可自定义大小
        Thumbnails.of(file2).scale(0.25f).toFile(file2.getAbsolutePath()+"_25.jpg");
        System.out.println("图片的方法===="+file2.getAbsolutePath()+"_25.jpg");
        this.url =file.getOriginalFilename();
    }



    /**
     * 修改方法
     * 使用的jpa
     * @param user
     * @return
     */
    @RequestMapping("updateOne")
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
    @RequestMapping("insertOneUser")
    @ResponseBody
    @ApiOperation("这个一个用户的添加的接口")
    public ResponseResult insertOneUser(@RequestBody UserInfo user){
        ResponseResult responseResult = ResponseResult.getResponseResult();

        Long uuidOrder = Long.valueOf(UID.getUUIDOrder());
        user.setId(uuidOrder);
        System.out.println("添加的密码====》"+user.getPassword());
        String lcg = MD5.encryptPassword(user.getPassword(), "lcg");

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
    @RequestMapping("selectAllRoleAndMenu")
    @ResponseBody
    @ApiOperation("这个一个分页查询角色和角色拥有权限的的接口")
    public PageInfo<RoleInfo> selectAllRoleAndMenu(@RequestBody Map<String,Object> map){

        PageInfo<RoleInfo> roleInfoPageInfo = customerService.selectAllRoleAndMenu(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        return  roleInfoPageInfo;
    }

    /**
     * 查询所有的角色
     */
    @RequestMapping("selectAllRole")
    @ResponseBody
    @ApiOperation("这个一个查询角色的接口")
    public List<RoleInfo> selectAllRole(){
        List<RoleInfo> roleInfos = customerService.selectAllRole();
        return roleInfos;
    }

    //  <!--这是一个用户和角色关联查询的sql、主要查询角色的等级，id，名称 -->
    @RequestMapping("findRoleByUserId")
    @ResponseBody
    @ApiOperation("这是一个用户和角色关联查询的sql、主要查询角色的等级，id，名称 ")
    public ResponseResult findRoleByUserId(@RequestBody Map<String,Object> map){
        System.out.println("进入根据userid查询角色的方法");
        ResponseResult responseResult = ResponseResult.getResponseResult();
        UserInfo userid = customerService.findRoleByUserId(Long.parseLong(map.get("userid").toString()));
        System.out.println(userid);
        responseResult.setResult(userid);
        return responseResult;
    }
    /**
     * 用户绑定角色
     */
    @RequestMapping("insertRoleAndUserCertont")
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
    @RequestMapping("deleteOneRole")
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
    @RequestMapping("insertOneRole")
    @ResponseBody
    @ApiOperation("这个一个添加角色的接口")
    public ResponseResult insertOneRole(@RequestBody Map<String,Object>map){
        System.out.println("进入添加角色的方法");
        ResponseResult responseResult = ResponseResult.getResponseResult();

        System.out.println(map.get("roleids").toString()+"parent");
        customerService.insertOneRole(map.get("roleName").toString(),map.get("miaoShu").toString(),Integer.parseInt(map.get("leval").toString()),Long.parseLong(map.get("roleids").toString()));
        responseResult.setCode(200);
        return  responseResult;
    }

    /**
     * 查询权限
     */
    @RequestMapping("findMenu")
    @ResponseBody
    @ApiOperation("这个一个查询权限的接口")
    public ResponseResult findMenu(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        //根绝角色id查权限
        List<MenuInfo> menu = customerService.findMenu(Long.parseLong(map.get("roleids").toString()));

        responseResult.setResult(menu);
        return responseResult;
    }

    /**
     * 添加角色权限的绑定
     * 中间表添加
     */
    @RequestMapping("insertRoleAndMenuAndCentre")
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
    @RequestMapping("selectAllMenu")
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
    @RequestMapping("insertMenu")
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
    @RequestMapping("updateMenu")
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
    @RequestMapping("deleteMenu")
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



    @RequestMapping("sevenDayDate")
    @ResponseBody
    public ResponseResult sevenDayDate(){
        ResponseResult responseResult = ResponseResult.getResponseResult();

        return responseResult;
    }






    //导入
    @RequestMapping("addExcel")
    @ApiOperation("这是 UserInfoController 上传文件的方法")
    public void addExcel(@RequestParam("file")MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        //创建数组
        ArrayList<UserInfo> userInfos = new ArrayList<>();
        //打开HSSFWorkbook对象XSSFWorkbook
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheetAt = workbook.getSheetAt(0);
        //获取表单所有行
        int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();
        //向数据库导入数据
        for (int i = 1; i < physicalNumberOfRows; i++) {
            XSSFRow row = sheetAt.getRow(i);
            //创建对象
            UserInfo userInfo = new UserInfo();
            //第一列 如果Id为自增第一行列为空，从第二列开始，样式同导出



            XSSFCell c0 = row.getCell(0);
            System.out.println(c0+"========c0=============");
            c0.setCellType(c0.CELL_TYPE_STRING);
            //装入对象
            userInfo.setId(Long.parseLong(c0.getStringCellValue()));

            XSSFCell c1 = row.getCell(1);
            c1.setCellType(c1.CELL_TYPE_STRING);
            System.out.println(c1+"========c1=============");
            //装入对象
            userInfo.setUserName(c1.getStringCellValue());
            //第二列



            XSSFCell c3 = row.getCell(2);
            System.out.println(c3+"=========c3============");
            //装入对象;
            userInfo.setLoginName(c3.getStringCellValue());//登陆的账号


            //第三列
            XSSFCell c4 = row.getCell(3);
            System.out.println(c4+"==========c4===========");
            //装入对象
            userInfo.setPassword(c4.getStringCellValue());

            //第四列
            XSSFCell c5 = row.getCell(5);
            System.out.println(c5+"==========c5===========");
            //装入对象
            userInfo.setTel(c5.getStringCellValue());

            //五
            XSSFCell c6 = row.getCell(4);
            System.out.println(c6+"==========c6===========");
            //装入对象
            userInfo.setSex(c6.getStringCellValue());


            userInfo.setImgUrl(null);

            //把对象装入数组
            userInfos.add(userInfo);
        }
        //循环数组
        userInfos.forEach(userInfo -> {
            //把数值装入增加方法中
            customerService.insertOneUser(userInfo);
        });
    }


    /**
     * 根据名字验证是否存在该用户
     * @param map
     * @return
     */
    @RequestMapping("findUserByLoginName")
    @ResponseBody
    public ResponseResult findUserByLoginName(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        UserInfo username = customerService.findUserByLoginName(map.get("username").toString());
        if(username != null){
            responseResult.setCode(200);
            responseResult.setSuccess("有效用户");
           return  responseResult;
        }else{
            responseResult.setCode(500);
            responseResult.setError("该用户不存在");
            return responseResult;
        }

    }


    /**
     * 发送邮箱验证码
     * @param map
     * @return
     * @throws MessagingException
     */
    @RequestMapping("getsendEmail")
    @ResponseBody
    public ResponseResult getsendEmail(@RequestBody Map<String,Object> map) throws MessagingException {
        ResponseResult responseResult = ResponseResult.getResponseResult();
        String email = map.get("email").toString();
        System.out.println(email);
        UserInfo userByEmail = customerService.findUserByEmail(email);
        if(userByEmail != null){
            String randomString = this.getRandomString();
            EmailUtil.sendEmail(email,randomString);
            redisTemplate.opsForValue().set(email,randomString);
            redisTemplate.expire(email,2, TimeUnit.MINUTES);
            responseResult.setCode(200);
            responseResult.setSuccess("成功获取验证码,有效时间2分钟");
            return responseResult;
        }else{
            responseResult.setCode(500);
            responseResult.setError("邮箱错误/该邮箱未绑定该用户");
            return responseResult;
        }
    }

    @RequestMapping("sendEmail")
    @ResponseBody
    public ResponseResult sendEmail(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
         String email =  map.get("email").toString();
         String authcode =  map.get("authcode").toString();
        String authCode = redisTemplate.opsForValue().get(email);
        if(authcode.equals(authCode)){
            responseResult.setCode(200);
            responseResult.setSuccess("验证码校验成功");
            return responseResult;
        }else{
            responseResult.setCode(500);
            responseResult.setError("验证码错误/校对后输入");
            return responseResult;
        }

    }

    @RequestMapping("updatePasswordByLoginName")
    @ResponseBody
    public ResponseResult updatePasswordByLoginName(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = ResponseResult.getResponseResult();
        String password =  map.get("password").toString();

        String username =  map.get("username").toString();
        String pwd = MD5.encryptPassword(password, "lcg");
        customerService.updatePasswordByLoginName(username,pwd);
        redisTemplate.delete(map.get("email").toString());
        responseResult.setCode(200);
        responseResult.setSuccess("密码重置成功");
        return responseResult;
    }
    /**
     * 随机生成验证码
     * @return
     */
    public String getRandomString(){
        Random random = new Random();
        String result="";
        for (int i=0;i<6;i++)
        {
            result+=random.nextInt(10);
        }

        return result;
    }
}
