package com.cmx.shiroservice.controller;


import com.cmx.shiroapi.commons.ResultData;
import com.cmx.shiroapi.enums.SystemMessageEnum;
import com.cmx.shiroapi.model.dto.MainMenu;
import com.cmx.shiroapi.model.params.LoginSuccessInfo;
import com.cmx.shiroapi.service.SystemMenuService;
import com.cmx.shiroservice.manager.UserManager;
import com.cmx.shiroservice.service.DynamicShiroService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@Slf4j
public class LoginController {


    @Autowired
    private UserManager userManager;

    @Autowired
    private SystemMenuService systemMenuService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultData login(String username,
                            String password,
                            String rememberMe){
        log.info("login : userNmae : {} , password : {}", username, password);
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        token.setRememberMe(true);
        Subject subject = SecurityUtils.getSubject();
        try{
            subject.login(token);
        }catch(Exception e) {
            if(e instanceof LockedAccountException){
                log.info("登入次数过多，账户被锁定!");
                return ResultData.newFail(SystemMessageEnum.LOCKED_USER.getCode(), SystemMessageEnum.LOCKED_USER.getMsgCn());
            }else if(e instanceof UnknownAccountException){
                log.info("账户不存在或用户名密码未填写");
                return ResultData.newFail(SystemMessageEnum.USER_NOT_EXIT.getCode(), SystemMessageEnum.USER_NOT_EXIT.getMsgCn());
            }else if(e instanceof AuthenticationException){
                log.info("认证失败, {}", e.getMessage());
                return ResultData.newFail(SystemMessageEnum.AUTH_FAIL.getCode(), SystemMessageEnum.AUTH_FAIL.getMsgCn());
            }else{
                log.info("other error : {}", e);
                return ResultData.newFail(SystemMessageEnum.UNKNOWN_SYSTEM_ERROR.getCode(), SystemMessageEnum.UNKNOWN_SYSTEM_ERROR.getMsgCn());
            }
        }
        LoginSuccessInfo loginSuccess = userManager.getLoginSuccess(subject);

        return ResultData.newSingleSuccess(loginSuccess);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ResultData logout(){
        Subject subject = SecurityUtils.getSubject();
        String userName = null;
        if(subject.isAuthenticated()){
            userName = subject.getPrincipal().toString();
            subject.logout();
        }
        return ResultData.newSingleSuccess(userName);
    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    @ResponseBody
    public ResultData getMenu(String roleId){

        Map<String, Object> params = new HashMap<>();
        params.put("roleId", roleId);
        List<MainMenu> mainMenus = systemMenuService.distributeMenu(params);
        return ResultData.newSetSuccess(mainMenus, 0);
    }

}
