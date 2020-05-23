package com.zy.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

  /**
   * 分页的拦截器
   * @return
   */
  @Bean
  public PaginationInterceptor paginationInterceptor(){
    PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
    paginationInterceptor.setDialectType("mysql") ;
    return  paginationInterceptor ;
  }
}

