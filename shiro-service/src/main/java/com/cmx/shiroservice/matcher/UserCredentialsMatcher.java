package com.cmx.shiroservice.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.atomic.AtomicInteger;

public class UserCredentialsMatcher extends HashedCredentialsMatcher {


    @Value("${shiro.maxpassword.retrytime}")
    private Integer maxPasswordRetryTime;


    private Cache<String, AtomicInteger> passwordRetryCache;

    public UserCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String)token.getPrincipal();

        AtomicInteger retryCount = passwordRetryCache.get(username);
        System.out.println(username +" 验证次数 :" + retryCount);
        if(retryCount == null){
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }else{
            passwordRetryCache.put(username, new AtomicInteger( retryCount.incrementAndGet()));
        }

        if(retryCount.get() >= maxPasswordRetryTime){
            throw new LockedAccountException("账户被锁定10分钟");
        }

        boolean result = super.doCredentialsMatch(token, info);

        if(result){
            passwordRetryCache.remove(username);
        }

        return  result;
    }


    @Override
    public String getHashAlgorithmName() {
        return super.getHashAlgorithmName();
    }


    @Override
    public boolean isStoredCredentialsHexEncoded() {
        return super.isStoredCredentialsHexEncoded();
    }

    @Override
    public void setStoredCredentialsHexEncoded(boolean storedCredentialsHexEncoded) {
        super.setStoredCredentialsHexEncoded(storedCredentialsHexEncoded);
    }


    @Override
    public int getHashIterations() {
        return super.getHashIterations();
    }

    @Override
    public void setHashIterations(int hashIterations) {
        super.setHashIterations(hashIterations);
    }
}
