package com.zy.config;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration {

  private SmsProperties smsProperties ;

  /**
   * spring 在构建config 时，会自动的往构造器里面注入ioc 容器里面的对象
   * @param smsProperties
   */
  public SmsAutoConfiguration(SmsProperties smsProperties){
    this.smsProperties = smsProperties ;
  }

  /**
   * 用来发送短信的客户端
   * @return
   */
  @Bean
  public IAcsClient iAcsClient(){
    DefaultProfile profile = DefaultProfile.getProfile(smsProperties.getRegionId(), smsProperties.getAccessKeyId(), smsProperties.getAccessSecret());
    IAcsClient client = new DefaultAcsClient(profile);
    return client ;
  }
}

