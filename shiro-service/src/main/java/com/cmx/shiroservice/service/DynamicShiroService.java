package com.cmx.shiroservice.service;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DynamicShiroService {

    @Autowired
    ShiroFilterFactoryBean shiroFilterFactoryBean;


    public Map<String, String> reloadShiroFiterChainMap(){
        //从数据库重新获取权限

        return null;
    }


    public void updateDynamicPermission(){
        synchronized(shiroFilterFactoryBean){
            AbstractShiroFilter shiroFilter;
            try{
                shiroFilter = (AbstractShiroFilter)shiroFilterFactoryBean.getObject();
            }catch(Exception e){
                throw new RuntimeException("get shiroFilterFactoryBean fail !");
            }

            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver)shiroFilter.getFilterChainResolver();
            DefaultFilterChainManager filterChainManager = (DefaultFilterChainManager)filterChainResolver.getFilterChainManager();

            //清除原先的权限
            filterChainManager.getFilterChains().clear();
            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();
            shiroFilterFactoryBean.setFilterChainDefinitionMap(reloadShiroFiterChainMap());
            Map<String, String> chains = shiroFilterFactoryBean.getFilterChainDefinitionMap();
            for(Map.Entry<String, String> entry : chains.entrySet()){
                String url = entry.getKey();
                String chainDefinition = entry.getValue().trim();
                filterChainManager.createChain(url, chainDefinition);
            }

        }
    }



}
