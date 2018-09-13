package com.cmx.shiroweb.chat.enums;

public enum MessageType {

    LOGIN(0, "login", "登入消息"),
    USER(1, "user", "对话消息"),
    SYSTEM(2, "system", "系统消息"),
    EMJO(3, "emjo", "表情");

    private int code;
    private String msg;
    private String dec;

    MessageType(int code, String msg, String dec){
        this.code = code;
        this.msg = msg;
        this.dec = dec;
    }

}
