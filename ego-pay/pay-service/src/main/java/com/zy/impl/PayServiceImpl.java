package com.zy.impl;


import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.zy.config.AilipayProperties;
import com.zy.domain.Pay;
import com.zy.service.PayService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

  @Autowired
  private AlipayTradeService tradeService;

  @Autowired
  private AlipayClient alipayClient;
  @Autowired
  private AilipayProperties ailipayProperties;

  @Override
  public String pay(Pay pay) {
    switch (pay.getPayType()) {
      case 0: // 扫描支付
        return getQrCode(pay);
      case 1: // 网页支付
        return getPayHtml(pay);
      default:
        throw new IllegalArgumentException("支付类型暂不支持!");
    }

  }

  /**
   * 网页支付的实现
   *
   * @param pay
   * @return
   */
  private String getPayHtml(Pay pay) {
//    AlipayTradePrecreateRequest alipayTradePrecreateRequest = new AlipayTradePrecreateRequest();
    AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
    alipayTradePagePayRequest.setReturnUrl(ailipayProperties.getReturnUrl());
    alipayTradePagePayRequest.setNotifyUrl(ailipayProperties.getNotifyUrl());
    alipayTradePagePayRequest.setBizContent(JSON.toJSONString(pay));
    String result=null;
    try {

       result = alipayClient.pageExecute(alipayTradePagePayRequest).getBody();
    } catch (AlipayApiException e) {
      e.printStackTrace();
      System.out.println("生成支付的网页使用，原因为:"+e.getErrMsg());
      return  null;
    }
    return  result;
  }
  public boolean rsaCheckV1(Map<String, String> params)
  {
    boolean signVerified = false;
    try
    {
      signVerified = AlipaySignature.rsaCheckV1(params, this.ailipayProperties
        .getAlipayPrivateKey(), this.ailipayProperties
        .getCharset(), this.ailipayProperties
        .getSignType());
    }
    catch (AlipayApiException e)
    {
      e.printStackTrace();
      signVerified = false;
    }
    return signVerified;
  }

  private String getQrCode(Pay pay) {
    // 创建扫码支付请求builder，设置请求参数
    AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
      .setSubject(pay.getSubject())
      .setTotalAmount(pay.getTotalAmount())
      .setOutTradeNo(pay.getOutTradeNo())
      .setUndiscountableAmount(pay.getUndiscountableAmount())
      .setSellerId(pay.getSellerId())
      .setBody(pay.getBody())
      .setOperatorId(pay.getOperatorId())
      .setStoreId(pay.getStoreId())
      .setTimeoutExpress(pay.getTimeoutExpress()).
        setNotifyUrl(String.format(ailipayProperties.getNotifyUrl(),pay.getOutTradeNo()));

    //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
    // 类似发一个Http的请求
    AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
    String returnResult = null;
    switch (result.getTradeStatus()) {
      case SUCCESS:
        returnResult = result.getResponse().getQrCode();
        break;
      case FAILED:
        System.out.println("支付宝预下单失败!!!");
        break;
      case UNKNOWN:
        System.out.println("系统异常，预下单状态未知!!!");
        break;
      default:
        System.out.println("不支持的交易状态，交易返回异常!!!");
        break;
    }
    return returnResult;
  }

}