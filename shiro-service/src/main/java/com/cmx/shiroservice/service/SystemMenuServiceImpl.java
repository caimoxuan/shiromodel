package com.cmx.shiroservice.service;

import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.model.SystemMenu;
import com.cmx.shiroapi.model.dto.MainMenu;
import com.cmx.shiroapi.model.dto.MenuParamQueryDTO;
import com.cmx.shiroapi.model.dto.SubMenu;
import com.cmx.shiroapi.service.SystemMenuService;
import com.cmx.shiroservice.mapper.SystemMenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Slf4j
@Service
public class SystemMenuServiceImpl implements SystemMenuService {


    @Autowired
    private SystemMenuMapper systemMenuMapper;

    @Override
    public SystemMenu findMenu(Integer id) {
        return systemMenuMapper.getById(id);
    }

    @Override
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

    @Override
    public List<SystemMenu> getMenuByRole(Map<String, Object> params){
        return systemMenuMapper.queryByRole(params);
    }


    @Override
    public void deleteMenu(Integer id) {
        systemMenuMapper.delete(id);
    }

    @Override
    public void modifyMenu(SystemMenu systemMenu) {
        systemMenuMapper.modify(systemMenu);
    }

    @Override
    public void addMenu(SystemMenu systemMenu){
        systemMenuMapper.add(systemMenu);
    }

    @Override
    public ResultData queryMenu(MenuParamQueryDTO menuParamQueryDTO) {

        Map<String, Object> params = getParams(menuParamQueryDTO);
        List<SystemMenu> systemMenuList = systemMenuMapper.query(params);
        return ResultData.newSetSuccess(systemMenuList, systemMenuMapper.countByFilter(params).intValue());
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

    private Map<String, Object> getParams(MenuParamQueryDTO menuParamQueryDTO){
        Map<String, Object> params = new HashMap<>();
        params.put("menuType",menuParamQueryDTO.getMenuType());
        params.put("sequence", menuParamQueryDTO.getSequence());
        params.put("startPage", (menuParamQueryDTO.getStartPage()-1)*menuParamQueryDTO.getPageSize());
        params.put("endPage", menuParamQueryDTO.getStartPage()*menuParamQueryDTO.getPageSize());
        return params;
    }
}
