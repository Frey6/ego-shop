package com.zy;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import javax.xml.crypto.Data;

@SpringBootApplication
@MapperScan("com.zy.mapper")
@EnableDubbo
public class ManagerApp {
  public static void main(String[] args) {
    SpringApplication.run(ManagerApp.class);
  }
}
