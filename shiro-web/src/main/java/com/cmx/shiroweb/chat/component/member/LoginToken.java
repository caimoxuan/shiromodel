package com.cmx.shiroweb.chat.component.member;

import lombok.Data;

/**
 * @author cmx
 * @date 2019/3/6
 */
@Data
public class LoginToken {

    /**
     * 当前用户的用户id
     */
    private String userId;

    /**
     * 当前用户的token 用于校验
     */
    private String token;

}
