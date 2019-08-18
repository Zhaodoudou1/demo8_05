package com.zdd.pojo.entity;

import com.zdd.pojo.base.BaseAuditable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Data
@Table(name = "base_role")
public class RoleInfo extends BaseAuditable {

    @Column(name = "roleName")
    private String roleName;

    @Column(name = "miaoShu")
    private String miaoShu;

    @Column(name = "parentid")
    private Long parentid;//创建角色时该角色的id

    @Column(name = "leval")
    private  int leval;//角色级别

    @Transient
    private String menus;

    @Transient
    private String[] ids;

    @Transient
    private String mids;

}
