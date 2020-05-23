package com.zy;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class PayServiceApp {
  public static void main(String[] args) {
    SpringApplication.run(PayServiceApp.class);
  }
}
