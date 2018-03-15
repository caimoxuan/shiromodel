package com.cmx.shiroapi.enums;

public enum MenuType implements BaseEnum{

    SYSTEM_MENU(0, "系统使用菜单规则"),
    HTML_MENU(1, "页面使用菜单规则"),
    BUTTOM_MENU(2, "按钮使用菜单规则"),
    UNKNOW_CODE(4, "未知的菜单规则");

    MenuType(int code, String message){
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static MenuType valueOf(int code){
        for(MenuType menuType : MenuType.values()){
            if(menuType.getCode() == code){
                return menuType;
            }
        }
        return UNKNOW_CODE;
    }


}
