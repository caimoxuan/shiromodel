package com.cmx.shiroapi.service;

import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.model.SystemMenu;
import com.cmx.shiroapi.model.dto.MainMenu;
import com.cmx.shiroapi.model.dto.MenuParamQueryDTO;

import java.util.List;
import java.util.Map;

public interface SystemMenuService {

    SystemMenu findMenu(Integer id);

    /**
     * 查询用户所属的角色拥有的所有菜单
     * @param params
     * @return
     */
    List<MainMenu> distributeMenu(Map<String, Object> params);

    void deleteMenu(Integer id);

    void modifyMenu(SystemMenu systemMenu);

    void addMenu(SystemMenu systemMenu);

    /**
     * 分页查询相应的菜单
     * @param menuParamQueryDTO
     * @return
     */
    ResultData queryMenu(MenuParamQueryDTO menuParamQueryDTO);

    List<SystemMenu> getMenuByRole(Map<String, Object> params);

}
