package com.zy;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.zy.mapper")
@EnableDubbo
@EnableCaching
public class ItemServiceApp {
  public static void main(String[] args) {

    SpringApplication.run(ItemServiceApp.class);
  }
}
