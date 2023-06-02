package com.alick.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author alick
 * @since 2023/1/9
 */
@Slf4j//是lombok中提供的注解, 用来通过slf4j记录日志。
@SpringBootApplication//标注是springboot项目的启动类，自动把配置组件等资源进行加载
@ServletComponentScan//在SpringBoot项目中, 在引导类/配置类上加了该注解后, 会自动扫描项目中(当前包及其子包下)的@WebServlet ,
// @WebFilter , @WebListener 注解, 自动注册Servlet的相关组件 ;
@EnableTransactionManagement//开启对事物管理的支持，在service的实现类中加入@Transactional即可
@EnableCaching // 开启缓存SpringCache，通过注解的方式
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功..");
    }
}
