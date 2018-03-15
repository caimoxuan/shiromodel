package com.cmx.shiroapi.model;


import com.cmx.shiroapi.enums.MenuType;
import lombok.Data;

import java.io.Serializable;

@Data
public class SystemMenu implements Serializable{

    private static final long serialVersionUID = -4252934566919118142L;

    private Integer id;
    private String menuUrl;
    private String menuIcon;
    private String menuCode;
    private String menuName;
    private String parentMenuCode;
    private Long sequence;
    private MenuType menuType;
    private String createTime;

}
