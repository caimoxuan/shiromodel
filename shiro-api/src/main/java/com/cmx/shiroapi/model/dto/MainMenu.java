package com.cmx.shiroapi.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class MainMenu implements Serializable {

    private String index;
    private String icon;
    private String title;
    private List<SubMenu> subs;

}
