package com.zy.config;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrikaConfig {

  /**
   * 从Orika 里面获取一个对象的映射工具
   * @return
   */
  @Bean
  public MapperFacade mapperFacade (){
    DefaultMapperFactory defaultMapperFactory = new DefaultMapperFactory.Builder().build();
    MapperFacade mapperFacade = defaultMapperFactory.getMapperFacade() ;
    return mapperFacade ;
  }
}

