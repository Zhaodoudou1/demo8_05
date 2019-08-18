package com.zdd.pojo.entity;

import com.zdd.pojo.base.BaseAuditable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "base_user")
public class UserInfo extends BaseAuditable {

    @Column(name = "userName")
   private String userName;

    @Column(name = "loginName")
   private String loginName;

    @Column(name = "password")
   private String password;

    @Column(name = "tel")
   private String tel;

    @Column(name = "sex")
   private String sex;

    @Column(name = "parentId")
    private Long parentId;

    @Column(name = "imgUrl")
    private String imgUrl;

    @Transient
    private Integer roleid;

    @Transient
    String ids;

    @Transient
    private String roleids;//接收角色id

    @Transient
    private String roleNames;//接收角色名称

    @Transient
    private int leval;

    @Transient
    private String[]  quanxian;

    @Transient
    private List<MenuInfo> listMenuInfo;

    @Transient
    private RoleInfo roleInfo;

    @Transient
    private Map<String,String> authmap;

    @Transient
     private Object[] LoginKeys;

    @Transient
    private String email;

    @Transient
    private  Object[] LoginValues;
}
