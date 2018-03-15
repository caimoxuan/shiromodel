package com.cmx.shiroapi.service;

import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.model.SystemRole;
import com.cmx.shiroapi.model.dto.RoleParamQueryDTO;

import java.util.List;

public interface SystemRoleService {

    void addRole(SystemRole systemRole);

    void modifyRole(SystemRole systemRole);

    void delRole(Integer roleId);

    ResultData<SystemRole> query(RoleParamQueryDTO roleParamQueryDTO);

    SystemRole getRoleByRoleId(Integer roleId);

    List<SystemRole> getRoleByUserId(Long userId);

    ResultData modifyRoleMenu(Integer roleId, List<Integer> menuCodes);

}
