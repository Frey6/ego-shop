package com.zy.config;

import com.zy.domain.SmsMessage;
import com.zy.domain.WechatMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import java.util.HashMap;

@Configuration
public class MqConfig  {
  @Bean
  public MessageConverter messageConverter(){
    MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
    mappingJackson2MessageConverter.setTypeIdPropertyName("JavaType");
    HashMap<String,Class<?>> objectObjectHashMap = new HashMap<>();
    objectObjectHashMap.put("smsMessage", SmsMessage.class);
    objectObjectHashMap.put("wechatMessage",WechatMessage.class);
    mappingJackson2MessageConverter.setTypeIdMappings(objectObjectHashMap);
    return  mappingJackson2MessageConverter;
  }

}
