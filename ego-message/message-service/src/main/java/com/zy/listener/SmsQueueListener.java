package com.zy.listener;


import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.zy.config.SmsProperties;
import com.zy.domain.SmsMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
@Component
@Slf4j
public class SmsQueueListener implements MessageListener {

  @Autowired
  private IAcsClient iAcsClient;

  @Autowired
  private SmsProperties smsProperties;


//  @Autowired
//  private MessageConverter messageConverter;

  /**
   * 消息的监听
   *
   * @param message
   */
  @JmsListener(destination = "sms.queue", concurrency = "3-5")
  @Override
  public void onMessage(Message message) {
    try {
//      SmsMessage smsMessage = (SmsMessage) messageConverter.fromMessage(message);
          ActiveMQObjectMessage  smsMessage   = (ActiveMQObjectMessage) message;
      sendMessage((SmsMessage) smsMessage.getObject());
    } catch (JMSException e) {
      e.printStackTrace();
    }
    System.out.println("收到消息了");
  }

  private void sendMessage(SmsMessage smsMessage) {
    log.info("模拟发送短信，内容为{}", smsMessage);
    CommonRequest commonRequest = new CommonRequest();
    commonRequest.setMethod(MethodType.POST);
    commonRequest.setDomain(smsProperties.getSysDomain());
    commonRequest.setVersion(smsProperties.getVersion());
    commonRequest.setAction("SendSms");
    commonRequest.putQueryParameter("RegionId", smsProperties.getRegionId());

    // 一下就是消息的内容
    commonRequest.putQueryParameter("PhoneNumbers", smsMessage.getPhoneNumbers());
    commonRequest.putQueryParameter("SignName", smsMessage.getSignName());
    commonRequest.putQueryParameter("TemplateCode", smsMessage.getTemplateCode());
    commonRequest.putQueryParameter("TemplateParam", smsMessage.getTemplateParam());
    commonRequest.putQueryParameter("SmsUpExtendCode", smsMessage.getSmsUpExtendCode());
    commonRequest.putQueryParameter("OutId", smsMessage.getOutId());

    try {
      CommonResponse commonResponse = iAcsClient.getCommonResponse(commonRequest);
      System.out.println(commonResponse.getData());
    } catch (ClientException e) {
      e.printStackTrace();
    }
  }
}
