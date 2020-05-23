package com.zy.listener;


import com.zy.config.WechatAutoConfiguration;
import com.zy.config.WechatProperties;
import com.zy.domain.WechatMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class WechatQueueListener implements MessageListener {

  @Autowired
  private MessageConverter messageConverter ;

  @Autowired
  private RestTemplate restTemplate ;

  @Autowired
  private WechatProperties wechatProperties ;

  @Autowired
  private WechatAutoConfiguration wechatAutoConfiguration ;

  @JmsListener(destination = "wechat.queue",concurrency = "5-10")
  @Override
  public void onMessage(Message message) {
    try {
      WechatMessage wechatMessage = (WechatMessage) messageConverter.fromMessage(message);
      sendWechatMessage(wechatMessage) ;
    } catch (JMSException e) {
      e.printStackTrace();
    }

  }

  private void sendWechatMessage(WechatMessage wechatMessage) { // 微信消息已经有了，现在怎么发送该消息
    // https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s
    String result = restTemplate.postForObject(
      String.format(wechatProperties.getMessageUrl(), wechatAutoConfiguration.getAccessToken()),
      wechatMessage, // restTempalte 底层会直接把对象转换为一个json 来发送，里面会使用jackson的消息转化器将对象->json
      String.class
    );
    System.out.println(result);

  }
}
