package com.zy.config;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties(WechatProperties.class)
@Slf4j
public class WechatAutoConfiguration {

  private WechatProperties wechatProperties ;

  @Autowired
  private RestTemplate restTemplate;

  //volatile 实现变量在线程里面可见，就是我刷新token的线程只要把该值修改了，其他的线程能里面看到被修改后的值
  private volatile String accessToken ; // 我们会定时刷新该token ，为了让其他线程方便看到更改的值，我们添加volatile

  public WechatAutoConfiguration(WechatProperties wechatProperties){
    this.wechatProperties  = wechatProperties ;
  }

  @Bean
  public RestTemplate restTemplate (){
    return  new RestTemplate() ;
  }

  @PostConstruct
  public void initToken(){  // 其他里面去获取
    refresh();
  }
  // 2 个小时获取一个Token
  @Scheduled(initialDelay = 7200*1000,fixedRate = 7200*1000)
  public void refresh(){
    //  https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
    String result = restTemplate.
      getForObject(
        String.format(wechatProperties.getTokenUrl(), wechatProperties.getAppId(), wechatProperties.getAppSecret()),
        String.class);
    JSONObject jsonObject = JSONUtil.parseObj(result);
    if(jsonObject.getStr("access_token")!=null){ // 代表本次获取成功了

      this.accessToken = jsonObject.getStr("access_token") ;
      log.info("刷新token 成功，值为{}",accessToken);
    }else{
      log.error("获取token失败，原因为{}",jsonObject);
    }
  }

  public String getAccessToken()
  {
    return this.accessToken;
  }
}
