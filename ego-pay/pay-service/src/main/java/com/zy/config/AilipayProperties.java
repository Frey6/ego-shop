package com.zy.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ailipay")
@Data
public class AilipayProperties {

  // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
  @ApiModelProperty("应用的Id")
  private  String appId;

  // 商户私钥，您的PKCS8格式RSA2私钥
  @ApiModelProperty("商户私钥")
  private  String merchantPrivateKey ;

  // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
  @ApiModelProperty("支付宝公钥")
  private  String alipayPrivateKey;

  // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
  @ApiModelProperty("服务器异步通知页面路径")
  private  String notifyUrl;

  // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
  @ApiModelProperty("服务器同步通知页面路径")
  private  String returnUrl;

  @ApiModelProperty("签名方式")
  private  String signType = "RSA2";

  @ApiModelProperty("字符编码格式")
  // 字符编码格式
  private  String charset = "utf-8";

  // 支付宝网关
  private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

}
