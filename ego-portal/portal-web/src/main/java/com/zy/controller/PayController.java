package com.zy.controller;


import com.zy.entity.OrderSettlement;
import com.zy.service.OrderService;
import com.zy.service.PayService;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class PayController
{
  @Reference(check=false)
  private PayService payService;
  @Reference(check=false)
  private OrderService orderService;

  @PostMapping({"/pay/{orderSn}"})
  public ResponseEntity<Void> notifyUrl(@PathVariable("orderSn") String orderSn, HttpServletRequest request)
  {
    System.out.println("有人来访问了");

    Map<String, String> params = new HashMap();
    Map<String, String[]> requestParams = request.getParameterMap();
    for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();)
    {
      String name = (String)iter.next();
      String[] values = (String[])requestParams.get(name);
      String valueStr = "";
      for (int i = 0; i < values.length; i++) {
        valueStr = valueStr + values[i] + ",";
      }
      params.put(name, valueStr);
    }
    boolean b = this.payService.rsaCheckV1(params);
    if (b)
    {
      System.out.println(orderSn);
      System.out.println("支付宝他来访问我了,通知我们付款成功了");

      this.orderService.setPayed(orderSn);
    }
    else
    {
      System.out.println("我已经记录你的ip，请谨慎操作");
    }
    return ResponseEntity.ok().build();
  }

  @PostMapping({"/p/order/pay"})
  @ApiOperation("完成订单的支付功能")
  public ResponseEntity<String> toPay(@RequestBody @Validated OrderSettlement orderSettlement)
  {
    String payUrl = this.orderService.toPay(orderSettlement);
    return ResponseEntity.ok(payUrl);
  }
//
  @GetMapping({"/p/order/query"})
  @ApiOperation("查询订单是否已经支付")
  public ResponseEntity<Boolean> queryOrderIsPayed(String orderSn)
  {
    Boolean isPayed = this.orderService.queryOrderIsPayed(orderSn);
    return ResponseEntity.ok(isPayed);
  }
}

