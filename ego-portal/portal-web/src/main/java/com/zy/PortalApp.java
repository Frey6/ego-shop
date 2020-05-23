package com.zy;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableDubbo
@MapperScan("com.zy.mapper")
public class PortalApp {
  public static void main(String[] args) {
//      http://localhost:8001/#/login
    SpringApplication.run(PortalApp.class);
  }
}
