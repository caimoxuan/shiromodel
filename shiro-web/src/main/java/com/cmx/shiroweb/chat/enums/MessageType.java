package com.cmx.shiroweb.chat.enums;

import lombok.Getter;

@Getter
public enum MessageType {

    SYSTEM(0, "system", "系统消息"),
    LOGIN(1, "login", "登入消息"),
    FILE(2, "file", "文件"),
    TEXT(1001, "text", "对话消息"),
    IMG(1002, "img", "图片"),
    EMOJI(1003, "emoji", "表情");



    private int code;
    private String msg;
    private String dec;

    MessageType(int code, String msg, String dec){
        this.code = code;
        this.msg = msg;
        this.dec = dec;
    }

}
