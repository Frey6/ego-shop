package com.zy.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wechat")
@Data
public class WechatProperties {
  @ApiModelProperty("微信的应用的id")
  private String appId;
  @ApiModelProperty("微信的密钥")
  private String appSecret;
  @ApiModelProperty("获取Tokenurl的地址")
  private String tokenUrl;
  @ApiModelProperty("发送微信模板消息的地址")
  private String messageUrl;
}
