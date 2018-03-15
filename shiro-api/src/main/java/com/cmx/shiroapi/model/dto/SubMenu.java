package com.cmx.shiroapi.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubMenu implements Serializable {

    private String index;
    private String title;
    private String icon;

}
