package com.cmx.shiroservice.service;

import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.model.SystemMenu;
import com.cmx.shiroapi.model.dto.MainMenu;
import com.cmx.shiroapi.model.dto.MenuParamQueryDTO;
import com.cmx.shiroapi.model.dto.SubMenu;
import com.cmx.shiroapi.service.SystemMenuService;
import com.cmx.shiroservice.manager.MenuManager;
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
    @Autowired
    private MenuManager menuManager;

    @Override
    public SystemMenu findMenu(Integer id) {
        return systemMenuMapper.getById(id);
    }

    @Override
    public List<MainMenu> distributeMenu(Map<String, Object> params) {
        List<MainMenu> mainMenus = menuManager.distributeMenu(params);
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


    private Map<String, Object> getParams(MenuParamQueryDTO menuParamQueryDTO){
        Map<String, Object> params = new HashMap<>();
        params.put("menuType",menuParamQueryDTO.getMenuType());
        params.put("sequence", menuParamQueryDTO.getSequence());
        params.put("startPage", (menuParamQueryDTO.getStartPage()-1)*menuParamQueryDTO.getPageSize());
        params.put("endPage", menuParamQueryDTO.getStartPage()*menuParamQueryDTO.getPageSize());
        return params;
    }
}
