package com.zy.test;


import com.zy.domain.Pay;
import com.zy.service.PayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PayServiceTest {
  @Autowired
  private PayService payService;

  @Test
  public  void test(){
    Pay pay = new Pay();
    pay.setPayType(0);
    pay.setOutTradeNo("2017064650102");
    pay.setSubject("egodsd商城");
    pay.setBody("购买了svwssdf");
    pay.setTotalAmount("20022");
    Object pay1 = payService.pay(pay);
    System.out.println(pay1);
  }
}
