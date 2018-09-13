package com.cmx.shiroapi.enums;

public enum SystemMessageEnum implements ErrorMessageEnum{

    //用户相关
    USER_NOT_EXIT(4001, "user not exit", "用户不存在"),
    AUTH_FAIL(4002, "user auth fail", "用户认证失败"),
    LOCKED_USER(4003, "locked user", "用户被锁定"),

    ENUM_NOT_FOND(5008, "enum not fond", "枚举为找到"),

    UNKNOWN_SYSTEM_ERROR(5000, "unknown system error", "未知的系统内部异常");


    private int code;
    private String msgEn;
    private String msgCn;

    SystemMessageEnum(int code, String msgEn, String msgCn){
        this.code = code;
        this.msgCn = msgCn;
        this.msgEn = msgEn;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage(){return msgCn; }

    public String getMsgEn() {
        return msgEn;
    }

    public String getMsgCn() {
        return msgCn;
    }

    public static SystemMessageEnum valueOf(int code){
        for(SystemMessageEnum sme : SystemMessageEnum.values()){
            if(sme.code == code){
                return sme;
            }
        }
        return ENUM_NOT_FOND;
    }
}
