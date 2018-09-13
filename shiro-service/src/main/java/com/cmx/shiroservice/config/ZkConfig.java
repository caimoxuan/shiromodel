package com.cmx.shiroservice.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ZkConfig {

        @Value("${dubbo.registry}")
        private String zkNodes;
        @Value("${dubbo.name}")
        private String appName;

}
