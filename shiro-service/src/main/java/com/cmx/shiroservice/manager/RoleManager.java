package com.cmx.shiroservice.manager;

import com.cmx.shiroservice.mapper.SystemRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RoleManager {

    @Autowired
    private SystemRoleMapper systemRoleMapper;

    @Transactional(rollbackFor = Exception.class)
    public void modifyRoleMenu(Integer roleId, List<Integer> menuCodes){
        //修改角色拥有的菜单
        systemRoleMapper.deleteRoleMenu(roleId);
        for(Integer menuCode : menuCodes){
            Map<String, Object> params = new HashMap<>(2);
            params.put("menuCode", menuCode);
            params.put("roleId", roleId);
            systemRoleMapper.createRoleMenu(params);
        }
    }

}
