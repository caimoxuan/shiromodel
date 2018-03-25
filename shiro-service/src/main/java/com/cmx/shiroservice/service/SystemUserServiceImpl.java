package com.cmx.shiroservice.service;


import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.enums.SystemMessageEnum;
import com.cmx.shiroapi.model.SystemUser;
import com.cmx.shiroapi.service.SystemUserService;
import com.cmx.shiroservice.manager.UserManager;
import com.cmx.shiroservice.mapper.SystemUserMapper;
import com.cmx.shiroservice.util.UserPasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class SystemUserServiceImpl implements SystemUserService {


    @Autowired
    private UserPasswordUtil passwordUtil;
    @Autowired
    private SystemUserMapper systemUserMapper;
    @Autowired
    private UserManager userManager;


    @Override
    public void addUser(SystemUser user) {
        //将用户密码加盐 加密保存
        passwordUtil.encryptPassword(user);
        systemUserMapper.add(user);
    }

    @Override
    public void updateUser(SystemUser user) {
        systemUserMapper.modify(user);
    }

    @Override
    public SystemUser findUserById(Long id) {
        return systemUserMapper.getById(id);
    }

    @Override
    public SystemUser findUserByName(String username) {
        return systemUserMapper.getByName(username);
    }

    @Override
    public List<SystemUser> findAllUser() {
        return systemUserMapper.query();
    }

    @Override
    public ResultData grantRole(String userName, List<Integer> roleIds){
        //将角色赋给用户
        /** 改变用户的角色 先将用户原先的角色删除， 再添加 */
        SystemUser user = systemUserMapper.getByName(userName);
        if(null == user){
            log.info("grantUserRole fail case user not find : userName : {}", userName);
            return ResultData.newFail(SystemMessageEnum.USER_NOT_EXIT.getCode(), SystemMessageEnum.USER_NOT_EXIT.getMsgCn());
        }

        userManager.grantRoleToUser(user.getId(), roleIds);
        return ResultData.newSingleSuccess(userName);
    }


}
