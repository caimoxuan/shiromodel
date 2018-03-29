package com.cmx.shiroapi.service;

import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.model.SystemUser;
import com.cmx.shiroapi.model.params.LoginSuccessInfo;

import java.util.List;

public interface SystemUserService {

    void addUser(SystemUser user);

    void updateUser(SystemUser user);

    SystemUser findUserById(Long id);

    SystemUser findUserByName(String username);

    List<SystemUser> findAllUser();

    ResultData grantRole(String userName, List<Integer> roleIds);

    LoginSuccessInfo loginSuccess();

}
