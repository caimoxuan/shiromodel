package com.cmx.shiroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class ShiroServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ShiroServiceApplication.class, args);
	}
}
