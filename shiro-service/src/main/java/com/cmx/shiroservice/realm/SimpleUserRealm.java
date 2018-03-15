package com.cmx.shiroservice.realm;

import com.cmx.shiroapi.model.SystemRole;
import com.cmx.shiroapi.model.SystemUser;
import com.cmx.shiroapi.service.SystemRoleService;
import com.cmx.shiroapi.service.SystemUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SimpleUserRealm extends AuthorizingRealm{


    @Autowired
    private SystemUserService systemUserService;
    @Autowired
    private SystemRoleService systemRoleService;



    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("进行权限查询");
        SystemUser user = (SystemUser)principalCollection.getPrimaryPrincipal();

        List<String> roles = new ArrayList<>();
        List<SystemRole> roleByUserId = systemRoleService.getRoleByUserId(user.getId());
        if (roleByUserId != null && roleByUserId.size() > 0) {
            for (SystemRole role : roleByUserId) {
                roles.add(role.getRoleName());
            }
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRoles(roles);
        getAuthorizationCache().put(user.getUsername(), simpleAuthorizationInfo);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        String userName = (String)authenticationToken.getPrincipal();
        Object password = authenticationToken.getCredentials();
        if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            throw new UnknownAccountException("账号或密码不能为空");
        }

        SystemUser user = systemUserService.findUserByName(userName);
        if(null == user){
            throw new UnknownAccountException("未知的账号");
        }

        if(user.getLocked()){
            throw new LockedAccountException("锁定的账号");
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user,user.getPassword(), ByteSource.Util.bytes(user.getCredentialsSalt()),getName());
        return authenticationInfo;
    }
}
