package com.cmx.shiroservice.controller;


import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.enums.SystemMessageEnum;
import com.cmx.shiroapi.model.SystemMenu;
import com.cmx.shiroapi.model.SystemRole;
import com.cmx.shiroapi.model.dto.MenuParamQueryDTO;
import com.cmx.shiroapi.model.dto.RoleParamQueryDTO;
import com.cmx.shiroapi.service.SystemMenuService;
import com.cmx.shiroapi.service.SystemRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
public class SystemManagerController {

    @Autowired
    private SystemMenuService menuService;
    @Autowired
    private SystemRoleService systemRoleService;

    @RequestMapping(value = "/menu-manager", method = RequestMethod.POST)
    public ResultData queryMenu(MenuParamQueryDTO menuParamQueryDTO){
        log.info("queryMenu get params : {}", menuParamQueryDTO);
        return menuService.queryMenu(menuParamQueryDTO);
    }

    @RequestMapping(value = "/menu-manager/addMenu", method = RequestMethod.POST)
    public ResultData addMenu(SystemMenu systemMenu){
        log.info("addMenu get params : {}", systemMenu);
        try {
            menuService.addMenu(systemMenu);
        }catch(Exception e){
            log.info("addMenu fail case : {}", e);
        }

        return ResultData.newSingleSuccess(systemMenu);
    }

    @RequestMapping(value = "/menu-manager/modifyMenu", method = RequestMethod.POST)
    public ResultData modifyMenu(SystemMenu systemMenu){
        log.info("modifyMenu get params : {}", systemMenu);
        try {
            menuService.modifyMenu(systemMenu);
        }catch(Exception e){
            log.info("modifyMenu fail case : {}", e);
        }

        return ResultData.newSingleSuccess(systemMenu);
    }


    @RequestMapping(value = "/menu-manager/delMenu", method = RequestMethod.POST)
    public ResultData deleteMenu(Integer[] ids){
        log.info("deleteMenu get params : {}", ids.length);
        try {
            for(Integer id : ids) {
                menuService.deleteMenu(id);
            }
        }catch(Exception e){
            log.info("deleteMenu fail case : {}", e);
        }

        return ResultData.newSingleSuccess(ids);
    }


    /** ------------------- role Manager start ---------------- */
    @RequestMapping(value = "/role-manager", method = RequestMethod.POST)
    public ResultData queryRole(RoleParamQueryDTO roleParamQueryDTO){
        log.info("queryRole get params : {}", roleParamQueryDTO);
        ResultData<SystemRole> result = systemRoleService.query(roleParamQueryDTO);
        return result;
    }

    @RequestMapping(value = "/role-manager/modifyRole", method = RequestMethod.POST)
    public ResultData modifyRole(SystemRole systemRole){
        log.info("modifyRole get params : {}", systemRole);
        try{
            systemRoleService.modifyRole(systemRole);
        }catch(Exception e){
            log.error("modify role get error : {}",e );
        }

        return ResultData.newSingleSuccess(systemRole);
    }

    @RequestMapping(value = "/role-manager/addRole", method = RequestMethod.POST)
    public ResultData addRole(SystemRole systemRole){
        log.info("addRole get params : {}", systemRole);
        try{
            systemRoleService.addRole(systemRole);
        }catch(Exception e){
            log.error("add role get error : {}",e );
        }
        return ResultData.newSingleSuccess(systemRole);
    }

    @RequestMapping(value = "/role-manager/delRole", method = RequestMethod.POST)
    public ResultData delRole(Integer[] ids){
        log.info("delRole get params : {}", ids.length);
        try{
            for(Integer roleId : ids) {
                systemRoleService.delRole(roleId);
            }
        }catch(Exception e){
            log.error("modify role get error : {}",e );
        }
        return ResultData.newSingleSuccess(ids);
    }

    @RequestMapping(value = "/role-manager/modifyRoleMenu", method = RequestMethod.POST)
    public ResultData modifyRoleMenu(Integer roleId, Integer[] menuCodes){
        log.info("modifyRoleMenu get params : roleId : {}, menuCode : {}", roleId, menuCodes.length);
        return systemRoleService.modifyRoleMenu(roleId, Arrays.asList(menuCodes));
    }

    @RequestMapping(value = "/role-manager/queryRoleMenu", method = RequestMethod.POST)
    public ResultData queryRoleMenu(Integer roleId){
        log.info("queryRoleMenu get params : {}", roleId);
        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);
        List<SystemMenu> rolesMenu = menuService.getMenuByRole(params);
        return ResultData.newSetSuccess(rolesMenu, rolesMenu.size());
    }

}
