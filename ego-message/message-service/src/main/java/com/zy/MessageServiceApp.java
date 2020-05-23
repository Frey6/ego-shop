package com.zy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MessageServiceApp {
  public static void main(String[] args) {
    SpringApplication.run(MessageServiceApp.class);
  }
}
