package com.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan({"com.oauth.**", "com.base.**"})
public class OauthResourcesServerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(OauthResourcesServerApplication.class, args);
    }

    // @Bean
    // public BCryptPasswordEncoder bCryptPasswordEncoder() {
    // return new BCryptPasswordEncoder();
    // }
}
