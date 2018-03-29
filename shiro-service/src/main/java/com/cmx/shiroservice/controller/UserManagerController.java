package com.cmx.shiroservice.controller;

import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.enums.SystemMessageEnum;
import com.cmx.shiroapi.model.SystemRole;
import com.cmx.shiroapi.model.SystemUser;
import com.cmx.shiroapi.service.SystemRoleService;
import com.cmx.shiroapi.service.SystemUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@Slf4j
@RestController
public class UserManagerController {

    @Autowired
    private SystemUserService systemUserService;
    @Autowired
    private SystemRoleService systemRoleService;

    @RequestMapping(value = "/user-manager", method = RequestMethod.POST)
    public ResultData getUserRoles(String userName){
      log.info("getUser get params : {}", userName);
      SystemUser user = systemUserService.findUserByName(userName);
      if(null == user){
          return ResultData.newFail(SystemMessageEnum.USER_NOT_EXIT.getCode(), SystemMessageEnum.USER_NOT_EXIT.getMsgCn());
      }
      List<SystemRole> roleList = systemRoleService.getRoleByUserId(user.getId());
      return ResultData.newSetSuccess(roleList, roleList.size());
    }


    @RequestMapping(value = "/user-manager/findUserRole", method = RequestMethod.POST)
    public ResultData getUsersRole(String userName){
        log.info("getUsersRole get params : {}", userName);
        SystemUser systemUser = systemUserService.findUserByName(userName);
        if(null != systemUser){
            List<SystemRole> roles = systemRoleService.getRoleByUserId(systemUser.getId());
            return ResultData.newSetSuccess(roles, roles.size());
        }
        return ResultData.newFail(SystemMessageEnum.USER_NOT_EXIT.getCode(), SystemMessageEnum.USER_NOT_EXIT.getMsgCn());
    }


    @RequestMapping(value = "/user-manager/modifyUserRole", method = RequestMethod.POST)
    public ResultData modifyUserRole(String userName, Integer[] roleIds){
        log.info("modifyUserRole get params -> userName : {} , roleIds : {}", userName, roleIds.length);
        return systemUserService.grantRole(userName, Arrays.asList(roleIds));
    }

}
