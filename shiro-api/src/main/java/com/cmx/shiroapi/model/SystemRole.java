package com.cmx.shiroapi.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SystemRole implements Serializable {

    private static final long serialVersionUID = 5435410448937134080L;
    /** 角色的id*/
    private Integer roleId;
    /** 角色代码 越小权限越高*/
    private Integer roleCode;
    /** 角色的详细信息*/
    private String description;
    /** 角色的名称*/
    private String roleName;

}
