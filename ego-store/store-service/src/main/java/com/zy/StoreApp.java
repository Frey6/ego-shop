package com.zy;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.zy.mapper")
@EnableCaching
@EnableAspectJAutoProxy(exposeProxy = true)
public class StoreApp {
  public static void main(String[] args) {
    SpringApplication.run(StoreApp.class,args);
  }
}
