package com.zy.controller;


import cn.hutool.json.JSONObject;
import com.zy.domain.SmsMessage;
import com.zy.domain.WechatMessage;
import com.zy.entity.User;
import com.zy.service.UserCollectionService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController
{
  @Reference(check=false)
  private UserCollectionService userCollectionService;

  private static final String USER_BIND_PHONE_PREFIX = " user-bind-phone:";
  @Autowired
  private StringRedisTemplate redisTemplate;
  @Autowired
  private JmsTemplate jmsTemplate;

  @GetMapping({"/p/user/collection/isCollection"})
  public ResponseEntity<Boolean> isCollection(Long prodId)
  {
    User user = (User) SecurityUtils.getSubject().getPrincipal();
    Boolean isCollection = this.userCollectionService.isCollection(user.getUserId(), prodId);
    return ResponseEntity.ok(isCollection);
  }

  @PostMapping({"/p/user/collection/addOrCancel"})
  public ResponseEntity<Void> addOrCancel(@RequestBody Long prodId)
  {
    User user = (User)SecurityUtils.getSubject().getPrincipal();
    this.userCollectionService.addOrCancel(user.getUserId(), prodId);
    return ResponseEntity.ok().build();
  }
//
  @PostMapping({"p/sms/send"})
  public ResponseEntity<Void> bindPhoneNum(@RequestBody Map<String, String> phonenum)
  {
    String code = genValidate(4);

    this.redisTemplate.opsForValue().set(" user-bind-phone:" + (String)phonenum.get("phonenum"), code, 1L, TimeUnit.MINUTES);

    SmsMessage smsMessage = new SmsMessage();
    smsMessage.setPhoneNumbers((String)phonenum.get("phonenum"));
    smsMessage.setSignName("SXT教育");
    smsMessage.setTemplateCode("SMS_183791509");
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("code", code);
    smsMessage.setTemplateParam(jsonObject.toString());

    this.jmsTemplate.convertAndSend("sms.queue", smsMessage);

    return ResponseEntity.ok().build();
  }

  private String genValidate(int i)
  {
    return "8888";
  }

  @GetMapping("/wehcat/message/push")
  public ResponseEntity<Void> sendWechatMsg(String user,String userName){
    WechatMessage wechatMessage = new WechatMessage();
    wechatMessage.setToUser(user); // 发送给你的那个用户(粉丝)
    wechatMessage.setTemplateId("iUT2XN6_OJv0L-pAjoQ_nax1YTv4_-bqsxVA5ki4V6k");
    wechatMessage.setUrl("www.chaohuiwan.top");
    wechatMessage.setTopColor("#FF0000");
    Map<String, Map<String, String>> data = new HashMap<>();
    data.put("user", WechatMessage.buildProp(userName,"#173177")) ;
    data.put("num",WechatMessage.buildProp("0570","#173177")) ;
    data.put("tradeTime",WechatMessage.buildProp(new Date().toString(),"#173177")) ;
    data.put("tradeType",WechatMessage.buildProp("买女朋友","#173177")) ;
    data.put("tradeMoney",WechatMessage.buildProp("人民币10,000,000.00元","#173177")) ;
    data.put("time",WechatMessage.buildProp(new Date().toString(),"#173177")) ;
    data.put("money",WechatMessage.buildProp("人民币102,080,002,000.00元","#173177")) ;
    wechatMessage.setData(data);
    jmsTemplate.convertAndSend("wechat.queue",wechatMessage);
    return ResponseEntity.ok().build() ;
  }

//
//  @GetMapping({"/wehcat/message/push"})
//  public ResponseEntity<Void> sendWechatMsg(String user, String userName)
//  {
//    WechatMessage wechatMessage = new WechatMessage();
//    wechatMessage.setToUser(user);
//    wechatMessage.setTemplateId("9CsyiOm9qFjnQ4iPhwzfBQY7foe7X13r8j3tuNDHbAk");
//    wechatMessage.setUrl("www.whsxt.com");
//    wechatMessage.setTopColor("#FF0000");
//    Map<String, Map<String, String>> data = new HashMap();
//    data.put("user", WechatMessage.buildProp(userName, "#173177"));
//    data.put("num", WechatMessage.buildProp("0570", "#173177"));
//    data.put("tradeTime", WechatMessage.buildProp("03��11�� 17��00��", "#173177"));
//    data.put("tradeType", WechatMessage.buildProp("��Mac", "#173177"));
//    data.put("tradeMoney", WechatMessage.buildProp("������2000.00��", "#173177"));
//    data.put("time", WechatMessage.buildProp("03��11��17��00��", "#173177"));
//    data.put("money", WechatMessage.buildProp("������102,080,002,000.00��", "#173177"));
//    wechatMessage.setData(data);
//    this.jmsTemplate.convertAndSend("wechat.queue", wechatMessage);
//    return ResponseEntity.ok().build();
//  }
}

