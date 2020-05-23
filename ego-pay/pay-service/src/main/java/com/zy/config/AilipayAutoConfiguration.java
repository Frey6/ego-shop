package com.zy.config;


import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AilipayProperties.class})
public class AilipayAutoConfiguration
{
  private AilipayProperties ailipayProperties;

  public AilipayAutoConfiguration(AilipayProperties ailipayProperties)
  {
    this.ailipayProperties = ailipayProperties;

    Configs.init("zfbinfo.properties");
  }

  @Bean
  public AlipayTradeService alipayTradeService()
  {
    return new AlipayTradeServiceImpl.ClientBuilder().build();
  }

  @Bean
  public AlipayClient alipayClient()
  {
    return new DefaultAlipayClient(
      this.ailipayProperties.getGatewayUrl(),
      this.ailipayProperties.getAppId(),
      this.ailipayProperties.getMerchantPrivateKey(), "json",
      this.ailipayProperties.getCharset(),
      this.ailipayProperties.getAlipayPrivateKey(),
      this.ailipayProperties.getSignType());
  }
}
