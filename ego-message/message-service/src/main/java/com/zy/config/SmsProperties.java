package com.zy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

  /**
   * 区域信息
   */
  private  String regionId ;

  /**
   * 访问的id
   */
  private String accessKeyId ;

  /**
   * 访问的密钥
   */
  private String accessSecret ;

  /**
   * 域名
   */
  private String sysDomain ;

  /**
   * 版本
   */
  private String version ;

}

