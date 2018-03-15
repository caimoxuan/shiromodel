package com.cmx.shiroapi.model.dto;


import com.cmx.shiroapi.enums.MenuType;
import lombok.Data;

@Data
public class MenuParamQueryDTO {

    private static final Integer DEFAULT_START_PAGE = 1;
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private Integer sequence;
    private MenuType menuType = MenuType.HTML_MENU;
    private String menuCode;
    private Integer startPage = DEFAULT_START_PAGE;
    private Integer pageSize = DEFAULT_PAGE_SIZE;


}
