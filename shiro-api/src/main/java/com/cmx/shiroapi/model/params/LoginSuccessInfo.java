package com.cmx.shiroapi.model.params;

import com.cmx.shiroapi.model.dto.MainMenu;
import lombok.Data;

import java.util.List;

@Data
public class LoginSuccessInfo {

    private String redirectUrl;
    private String userName;
    private String avatarUrl;
    private Long authFailTime;
    private List<MainMenu> menuList;

}
