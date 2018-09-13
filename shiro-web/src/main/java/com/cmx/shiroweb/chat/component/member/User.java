package com.cmx.shiroweb.chat.component.member;

import lombok.Data;

@Data
public abstract class User {

    protected Long userId;

    protected String userName;

    protected String desc;

    protected String privileges;

}
