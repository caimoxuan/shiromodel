package com.cmx.shiroservice.manager;


import com.cmx.shiroapi.model.SystemRole;
import com.cmx.shiroapi.model.SystemUser;
import com.cmx.shiroapi.model.dto.MainMenu;
import com.cmx.shiroapi.model.params.LoginSuccessInfo;
import com.cmx.shiroapi.service.SystemMenuService;
import com.cmx.shiroservice.comparator.SystemRoleComparator;
import com.cmx.shiroservice.mapper.SystemRoleMapper;
import com.cmx.shiroservice.mapper.SystemUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class UserManager {

    @Value("${shiro.success.url}")
    private String successUtl;

    @Value("${shiro.login.url}")
    private String loginUrl;

    @Autowired
    private SystemUserMapper systemUserMapper;
    @Autowired
    private SystemRoleMapper systemRoleMapper;
    @Autowired
    private SystemMenuService systemMenuService;

    public LoginSuccessInfo getLoginSuccess(Subject subject){
        LoginSuccessInfo loginSuccessInfo = new LoginSuccessInfo();
        if(subject.isAuthenticated()) {
            loginSuccessInfo.setRedirectUrl(successUtl);
        }else{
            loginSuccessInfo.setRedirectUrl(loginUrl);
        }
        Session session = subject.getSession();
        loginSuccessInfo.setUserName(((SystemUser)subject.getPrincipal()).getUsername());
        loginSuccessInfo.setAuthFailTime(session.getLastAccessTime().getTime());
        loginSuccessInfo.setAvatorUrl("https://upload.jianshu.io/users/upload_avatars/4361488/a7779618-da21-4d88-ab6e-4684c7cb327e.jpg?imageMogr2/auto-orient/strip|imageView2/1/w/240/h/240");
        loginSuccessInfo.setSystemMenu(getUserMenu(subject));
        return loginSuccessInfo;
    }


    public List<MainMenu> getUserMenu(Subject subject){
        if(!subject.isAuthenticated()){
            return null;
        }
        SystemUser user = (SystemUser)subject.getPrincipal();
        //查询用户相应的角色， 按用户的角色id来查找相应的菜单
        List<SystemRole> systemRole = systemRoleMapper.getRoleByUserId(user.getId());
        Map<String, Object> queryRoleMenuMap = new HashMap<>();
        if(systemRole.size() > 0){
            Optional<SystemRole> biggestRoleOption = systemRole.stream().min(new SystemRoleComparator());
            queryRoleMenuMap.put("roleId", biggestRoleOption.get().getRoleId());
        }else{
            queryRoleMenuMap.put("roleId", 5);
        }
        return systemMenuService.distributeMenu(queryRoleMenuMap);
    }


    public void registerUser(SystemUser systemUser){
        //用户注册
        //用户参数检测
        //用户创建
        //用户角色添加
    }

    //TODO 事务暂时不起作用 原因待查..
    @Transactional(rollbackFor = Exception.class)
    public void grantRoleToUser(Long userId, List<Integer> roleIds){
        //删除原先用户的所有角色
        systemUserMapper.deleteUserRole(userId);
        //添加相应的角色
        for(Integer roleId : roleIds){
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("roleId", roleId);
            systemUserMapper.createUserRole(params);
        }
    }
}
