package com.cmx.shiroweb.chat.component.member;

import lombok.Data;

@Data
public abstract class BaseUser {

    /**
     * 用户id
     */
    protected String userId;

    /**
     * 用户名称
     */
    protected String userName;

    /**
     * 排序号
     */
    protected String desc;

    /**
     * 权限码
     */
    protected String privileges;

    /**
     * 手机号
     */
    protected String phone;

    /**
     * 邮箱
     */
    protected String email;

}
