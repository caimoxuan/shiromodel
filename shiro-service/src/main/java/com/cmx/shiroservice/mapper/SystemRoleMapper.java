package com.cmx.shiroservice.mapper;


import com.cmx.shiroapi.model.SystemRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SystemRoleMapper {

    void add(SystemRole systemRole);

    void modify(SystemRole systemRole);

    void delete(Integer roleId);

    List<SystemRole> query(Map<String, Object> params);

    Long countByFilter(Map<String, Object> params);

    SystemRole getRoleById(Integer roleId);

    List<SystemRole> getRoleByUserId(Long userId);

    void deleteRoleMenu(Integer roleId);
    void createRoleMenu(Map<String, Object> params);

}
