package com.cmx.shiroservice.config;


import com.cmx.shiroservice.filter.ShiroAxiosFilter;
import com.cmx.shiroservice.matcher.UserCredentialsMatcher;
import com.cmx.shiroservice.realm.SimpleUserRealm;
import com.cmx.shiroservice.session.cache.RedisCacheManager;
import com.cmx.shiroservice.session.dao.RedisSessionDao;
import com.cmx.shiroservice.session.manager.RedisManager;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Value("${shiro.algorith.name}")
    private String algorithName;

    @Value("${shiro.hash.iterators}")
    private Integer hashIterator;

    @Value("${shiro.login.url}")
    private String loginUrl;

    @Value("${shiro.success.url}")
    private String successUrl;


    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //url设置
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        shiroFilterFactoryBean.setSuccessUrl(successUrl);

        //拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //匹配所有请求 authc 需要验证， anon 不需要验证 这里的权限可以使用动态添加的方式
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/logout", "anon");
        //其他所有的路径访问 要么的登入才能访问， 要么使用remember登入访问
        filterChainDefinitionMap.put("/**", "axios,authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        //配置自定义的拦截器
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("axios", new ShiroAxiosFilter()); //这里如果注入bean的话 直接就生效了
        shiroFilterFactoryBean.setFilters(filterMap);
        return shiroFilterFactoryBean;
    }


    /**
     *  开启shiro aop注解支持
     *  使用代理方式;所以需要开启代码支持
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator creator=new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }


    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm
        securityManager.setRealm(simpleUserRealm());
        //设置sessionManager
        securityManager.setSessionManager(sessionManager());
        //设置cacheManager
        securityManager.setCacheManager(redisCacheManger());
        securityManager.setRememberMeManager(cookieRememberMeManager());
        return securityManager;
    }


    @Bean
    public Realm simpleUserRealm(){
        SimpleUserRealm simpleUserRealm = new SimpleUserRealm();
        simpleUserRealm.setCredentialsMatcher(credentialsMatcher());
        simpleUserRealm.setAuthorizationCache(redisCacheManger().getCache("permissionCache"));
        return simpleUserRealm;
    }

    /**
     * 密码凭证匹配器
     * @return
     */
    @Bean
    public CredentialsMatcher credentialsMatcher(){
        UserCredentialsMatcher credentialsMatcher = new UserCredentialsMatcher(redisCacheManger());
        credentialsMatcher.setHashAlgorithmName(algorithName);
        credentialsMatcher.setHashIterations(hashIterator);
        //是否使用Hex方式 否则使用base64
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }

    /**
     * cacheManager  使用ecache缓存
     * @return
    @Bean
    public EhCacheManager ehCacheManager(){
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:shiro-ecache.xml");
        return cacheManager;
    }
     */

    /**
     * 使用redis作缓存
     * @return
     */
    @Bean
    public CacheManager redisCacheManger(){
        RedisCacheManager redisCacheManager =  new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }


    @Bean
    public SessionDAO redisSessionDao(){
        RedisSessionDao redisSessionDao = new RedisSessionDao();
        redisSessionDao.setRedisManager(redisManager());
        return redisSessionDao;
    }

    @Bean
    public RedisManager redisManager(){
        return new RedisManager();
    }

    @Bean
    public SessionManager sessionManager(){
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionDAO(redisSessionDao());
//        List<SessionListener> list = new ArrayList<>();
//        defaultWebSessionManager.setSessionListeners(list);   //会话监听列表
        defaultWebSessionManager.setDeleteInvalidSessions(true); //是否删除过期的session
        defaultWebSessionManager.setGlobalSessionTimeout(1800000);//设置过期的时间 毫秒为单位
        defaultWebSessionManager.setSessionIdCookie(sessionIdCookie());
        return defaultWebSessionManager;
    }

    //session and cookie start
    @Bean
    public CookieRememberMeManager cookieRememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberCookie());
        cookieRememberMeManager.setCipherKey(org.apache.shiro.codec.Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));//cipherKey是加密rememberMe Cookie的密钥；默认AES算法；
        return cookieRememberMeManager;
    }

    /**
     * 记住我功能 将用户名密码信息通过加密cookie返回页面
     * 但是记住我功能不安全，使用要慎重
     * @return
     */
    @Bean
    public SimpleCookie rememberCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    /**
     * cookie 生成模板
     */
    @Bean
    public SimpleCookie sessionIdCookie(){
        SimpleCookie simpleCookie = new SimpleCookie("sid-CMX");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(-1);// 30 天  -1 ：关闭浏览器失效
        return simpleCookie;
    }


}
