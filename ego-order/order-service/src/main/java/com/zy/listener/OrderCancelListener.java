package com.zy.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.entity.Order;
import com.zy.entity.OrderItem;
import com.zy.entity.OrderSettlement;
import com.zy.mapper.OrderItemMapper;
import com.zy.mapper.OrderMapper;
import com.zy.mapper.OrderSettlementMapper;
import com.zy.service.ProdService;
import com.zy.service.SkuService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderCancelListener  implements MessageListener {

  @Autowired
  private OrderMapper orderMapper ;

  @Autowired
  private OrderSettlementMapper orderSettlementMapper ;

  @Autowired
  private OrderItemMapper orderItemMapper ;

  @Reference(check = false)
  private SkuService skuService ;

  @Reference(check = false)
  private ProdService prodService ;

  @Autowired
  private JmsTemplate jmsTemplate ;

  @JmsListener(destination = "order.delay.queue")
  @Override
  public void onMessage(Message message) {
    try {
      ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
      String orderSn = textMessage.getText();
      Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNumber, orderSn));
      if(order!=null){
        order.setStatus(6);
        order.setCloseType((byte) -1);
        order.setCancelTime(new Date());
        orderMapper.updateById(order) ;
      }
      OrderSettlement orderSettlement = orderSettlementMapper.selectOne(new LambdaQueryWrapper<OrderSettlement>().eq(OrderSettlement::getOrderNumber, orderSn));
      if(orderSettlement!=null){
        orderSettlement.setIsClearing(0);
        orderSettlement.setPayStatus(-1);
        orderSettlementMapper.updateById(orderSettlement) ;
      }
      // 库存的回滚 ，prod库存回滚，sku的库存回滚
      List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNumber, orderSn));
      Map<Long,Integer> skuStocks = new HashMap<>(); // sku的回滚库存
      Map<Long,Integer> prodStocks = new HashMap<>(); // prod 的回滚库存
      for (OrderItem orderItem : orderItems) {
        skuStocks.put(orderItem.getSkuId(),orderItem.getProdCount()) ;
        Integer stock = prodStocks.get(orderItem.getProdId());
        if(stock!=null){
          stock +=  orderItem.getProdCount() ;
        }
        prodStocks.put(orderItem.getProdId(),stock) ;
      }
      prodService.addStocks(prodStocks);
      skuService.addStocks(skuStocks) ;
      jmsTemplate.convertAndSend("order.stock.add.queue",prodStocks);
    } catch (JMSException e) {
      e.printStackTrace();
    }

  }
}
