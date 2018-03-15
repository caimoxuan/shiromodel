package com.cmx.shiroapi.model.dto;

import lombok.Data;

@Data
public class RoleParamQueryDTO {

    private static final Integer DEFAULT_START_PAGE = 1;
    private static final Integer DEFAULT_PAGE_SIZE = 10;

    private Integer roleId;
    private Integer roleName;
    private Integer startPage = DEFAULT_START_PAGE;
    private Integer pageSize = DEFAULT_PAGE_SIZE;


}
