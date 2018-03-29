package com.cmx.shiroservice.manager;

import com.cmx.shiroapi.model.SystemMenu;
import com.cmx.shiroapi.model.dto.MainMenu;
import com.cmx.shiroapi.model.dto.SubMenu;
import com.cmx.shiroservice.mapper.SystemMenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@Slf4j
public class MenuManager {

    @Autowired
    private SystemMenuMapper systemMenuMapper;

    public List<MainMenu> distributeMenu(Map<String, Object> params) {
        log.info("distributeMenu params : {}", params);
        List<MainMenu> mainMenus = new ArrayList<>();
        List<SystemMenu> roleSystemMenu = systemMenuMapper.queryByRole(params);
        //找出其中的一级菜单(没有上级菜单编号的被认为是一级菜单)
        Stream<SystemMenu> systemMainMenuStream = roleSystemMenu.stream().filter(ele -> null == ele.getParentMenuCode());
        //找出其中的子菜单(有上级菜单的被认为是二级菜单)
        systemMainMenuStream.forEach(mainMenu -> {
            Stream<SystemMenu> systemSubMenu = roleSystemMenu.stream().filter(ele -> null != ele.getParentMenuCode());
            MainMenu mMenu = new MainMenu();
            mMenu.setIcon(mainMenu.getMenuIcon());
            mMenu.setTitle(mainMenu.getMenuName());
            mMenu.setIndex(mainMenu.getMenuUrl());
            mMenu.setSubs(convertMenu(systemSubMenu.filter(m -> m.getParentMenuCode().equals(mainMenu.getMenuCode()))));
            mainMenus.add(mMenu);
        });
        return mainMenus;
    }


    //将子菜单的流转换城subMenu类型
    private List<SubMenu> convertMenu(Stream<SystemMenu> systemSubMenuStream){
        List<SubMenu> subMenus = new ArrayList<>();
        systemSubMenuStream.forEach(subMenu -> {
            SubMenu sMenu = new SubMenu();
            sMenu.setIcon(subMenu.getMenuIcon());
            sMenu.setIndex(subMenu.getMenuUrl());
            sMenu.setTitle(subMenu.getMenuName());
            subMenus.add(sMenu);
        });
        return subMenus.size() > 0 ? subMenus : null;
    }
}
