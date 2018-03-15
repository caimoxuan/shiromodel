package com.cmx.shiroservice.mapper;


import com.cmx.shiroapi.model.SystemUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SystemUserMapper {

    void add(SystemUser user);

    void modify(SystemUser user);

    SystemUser getById(@Param("id") Long id);

    SystemUser getByName(@Param("username") String username);

    List<SystemUser> query();

    void deleteUserRole(Long userId);
    void createUserRole(Map<String,Object> params);


}
