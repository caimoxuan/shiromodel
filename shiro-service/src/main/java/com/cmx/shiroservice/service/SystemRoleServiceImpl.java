package com.cmx.shiroservice.service;

import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.model.SystemRole;
import com.cmx.shiroapi.model.dto.RoleParamQueryDTO;
import com.cmx.shiroapi.service.SystemRoleService;
import com.cmx.shiroservice.manager.RoleManager;
import com.cmx.shiroservice.mapper.SystemRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SystemRoleServiceImpl implements SystemRoleService{


    @Autowired
    private SystemRoleMapper systemRoleMapper;
    @Autowired
    private RoleManager roleManager;

    @Override
    public void addRole(SystemRole systemRole) {
        systemRoleMapper.add(systemRole);
    }

    @Override
    public void modifyRole(SystemRole systemRole) {
        systemRoleMapper.modify(systemRole);
    }

    @Override
    public void delRole(Integer roleId){
        systemRoleMapper.delete(roleId);
    }

    @Override
    public ResultData<SystemRole> query(RoleParamQueryDTO roleParamQueryDTO) {
        Map<String, Object> params = getParams(roleParamQueryDTO);
        List<SystemRole> systemRoles = systemRoleMapper.query(params);
        Long roleCount = systemRoleMapper.countByFilter(params);
        return ResultData.newSetSuccess(systemRoles, roleCount.intValue());
    }

    @Override
    public SystemRole getRoleByRoleId(Integer roleId) {
        return systemRoleMapper.getRoleById(roleId);
    }

    @Override
    public List<SystemRole> getRoleByUserId(Long userId) {
        return systemRoleMapper.getRoleByUserId(userId);
    }

    @Override
    public ResultData modifyRoleMenu(Integer roleId, List<Integer> menuCodes){
        roleManager.modifyRoleMenu(roleId, menuCodes);
        return ResultData.newSingleSuccess(roleId);
    }

    private Map<String, Object> getParams(RoleParamQueryDTO roleParamQueryDTO){
        Map<String, Object> params = new HashMap<>(4);
        params.put("roleName", roleParamQueryDTO.getRoleName());
        params.put("roleId", roleParamQueryDTO.getRoleId());
        params.put("startPage", (roleParamQueryDTO.getStartPage()-1)*roleParamQueryDTO.getPageSize());
        params.put("endPage", roleParamQueryDTO.getStartPage()*roleParamQueryDTO.getPageSize());
        return params;
    }
}
