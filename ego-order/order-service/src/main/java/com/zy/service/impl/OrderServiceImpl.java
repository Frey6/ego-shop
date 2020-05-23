package com.zy.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.domain.OrderVo;
import com.zy.domain.Pay;
import com.zy.domain.WechatMessage;
import com.zy.entity.Order;
import com.zy.entity.OrderItem;
import com.zy.entity.OrderSettlement;
import com.zy.entity.UserAddr;
import com.zy.mapper.OrderItemMapper;
import com.zy.mapper.OrderMapper;
import com.zy.mapper.OrderSettlementMapper;
import com.zy.model.ShopCartItem;
import com.zy.service.*;
import com.zy.service.com.zy.model.OrderResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Service(timeout = 10000,retries = 0)
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService{

  @Autowired
  private OrderMapper orderMapper ;

  @Autowired
  private OrderItemMapper orderItemMapper;

  @Autowired
  private OrderSettlementMapper orderSettlementMapper;

  @Reference(check = false)
  private PayService payService;
  @Reference(check = false)
//  @Autowired
  private BasketService basketService;

  @Reference(check = false)
  private SkuService skuService;
  @Reference(check = false)
  private ProdService prodService;

  @Autowired
  private JmsTemplate jmsTemplate;


  //订单状态 1:待付款 2:待发货 3:待收货 4:待评价 5:成功 6:失败
  @Override
  public OrderResult getOrderCount(String userId) {
    log.info("查询用户{}的订单详情",userId);
    OrderResult orderResult = new OrderResult();
    Integer unPay = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
      .eq(Order::getUserId, userId)
      .eq(Order::getStatus, 1)
    );
    Integer pay = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
      .eq(Order::getUserId, userId)
      .eq(Order::getStatus, 2)
    );
    Integer consignment = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
      .eq(Order::getUserId, userId)
      .eq(Order::getStatus, 3)
    );
    orderResult.setUnPay(unPay==null  ?  0:  unPay);
    orderResult.setPayed(pay ==null  ?  0:  pay);
    orderResult.setConsignment(consignment==null?0:consignment);
    return orderResult;
  }

  @Override
  public IPage<Order> findByPage(Page<Order> page, Order order) {
    page.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.desc("update_time")) ;
    IPage<Order> orderData = orderMapper.selectPage(page, new LambdaQueryWrapper<Order>()
      .eq(Order::getUserId, order.getUserId())
      .eq((order.getStatus() != null && order.getStatus() != 0), Order::getStatus, order.getStatus())
    );
    if(orderData.getRecords().isEmpty()){
      return orderData ;
    }
    List<Order> records = orderData.getRecords();
    for (Order record : records) {
      //getOrderNumber 和orderId 一样，OrderNumber也是唯一的一个
      record.setOrderItemDtos(getCartItems(record.getOrderNumber()));
    }
    return orderData ;
  }

  @Override
  public String toPay(OrderSettlement orderSettlement) {
    log.info("完成订单{}的支付",orderSettlement.getOrderNumber());
    OrderSettlement settlement = orderSettlementMapper.selectOne(new LambdaQueryWrapper<OrderSettlement>().
      eq(OrderSettlement::getOrderNumber, orderSettlement.getOrderNumber()));
     BigDecimal payAmount = settlement.getPayAmount();
    @NotBlank String orderNumber = settlement.getOrderNumber();
    Pay pay = new Pay();
    pay.setOutTradeNo(orderNumber);
    pay.setPayType(0);
    pay.setTotalAmount(payAmount.toString());
    pay.setSubject("【ego商城】"+orderNumber+"订单");
    pay.setBody("ego商城商品的购买");
    String payUrl = payService.pay(pay);
    return payUrl;
  }

  @Override
  public void setPayed(String orderSn) {
    log.info("设置订单{}已经支付",orderSn);
    Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderNumber, orderSn));
    if (order!=null){
      order.setStatus(2);
      order.setIsPayed(true);
      order.setUpdateTime(new Date());
      orderMapper.updateById(order);
    }
    OrderSettlement settlement = orderSettlementMapper.selectOne(new LambdaQueryWrapper<OrderSettlement>().
      eq(OrderSettlement::getOrderNumber, orderSn));
if (settlement!=null){
  settlement.setIsClearing(1);
  settlement.setPayType(0);
  settlement.setPayTypeName("支付宝扫描支付");
  orderSettlementMapper.updateById(settlement);
}

  }

  @Override
  public Boolean queryOrderIsPayed(String orderSn) {
    Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>().select(Order::getIsPayed).eq(Order::getOrderNumber, orderSn));

    return order.getIsPayed()==null?Boolean.FALSE:order.getIsPayed();
  }

  @Override
  public void toOrderPay(OrderVo orderVo) {
//    1.清空购物车
   basketService.clearCart(orderVo.getUserAddr().getUserId());
//    2.库存减少
    Map<Long, Integer> prodStocks = decrStocks(orderVo);

//    3.搜索引擎的更新
  jmsTemplate.convertAndSend("prod.decr.queue", JSONUtil.toJsonStr(prodStocks));
//    4.给用户发消息
    sendNofityToUser(orderVo.getUserAddr(),orderVo.getActualTotal(),orderVo.getOrderSn());
//    5.写订单的表
    writeOrder(orderVo);

    //6.订单没有支付则取消
    cancleOrderDealy(orderVo.getOrderSn());
//    Pay pay = new Pay();
//    pay.setPayType(0);
//    pay.setOutTradeNo(orderVo.getOrderSn());
//    pay.setTotalAmount(orderVo.getTotal().toString());
//    pay.setSubject("【ego商城订单】"+orderVo.getOrderSn());
//    pay.setBody("购买ego删除的订单");
//    String payUrl = payService.pay(pay);
//    return payUrl;

//    6.生成支付的链接
  }

  private void cancleOrderDealy(String orderSn) {
    jmsTemplate.convertAndSend("order.delay.queue", orderSn, new MessagePostProcessor() {
      @Override
      public Message postProcessMessage(Message message) throws JMSException {
        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,30*60*1000);
        return null;
      }
    });
  }

  private void sendNofityToUser(UserAddr userAddr, BigDecimal actualTotal, String orderSn) {
    WechatMessage wechatMessage = new WechatMessage() ;
    wechatMessage.setToUser("OnbgEDFAXaGW6YhtXtSA8SwUjJ4RJMUjMvv6fImjCc0");
    wechatMessage.setTemplateId("OnbgEDFAXaGW6YhtXtSA8SwUjJ4RJMUjMvv6fImjCc0");
    wechatMessage.setUrl("www.whsxt.com");
    Map<String, Map<String, String>> data = new HashMap<>();
    data.put("orderSn",WechatMessage.buildProp(orderSn,"#173177")) ;
    data.put("totalMoney",WechatMessage.buildProp(actualTotal.toString(),"#173177")) ;
    data.put("orderDate",WechatMessage.buildProp(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"),"#173177")) ;
    wechatMessage.setData(data);
    jmsTemplate.convertAndSend("wechat.queue",wechatMessage);
  }


  private void writeOrder(OrderVo orderVo) {
//    Order order = new Order();
//    OrderItem orderItem = new OrderItem();
//    OrderSettlement settlement = new OrderSettlement();
    List<ShopCartItem> shopCartItems = orderVo.getShopCartOrders().get(0).getShopCartItemDiscounts().get(0).getShopCartItems();
    StringBuilder stringBuilder = new StringBuilder();
    for (ShopCartItem shopCartItem : shopCartItems) {
      OrderItem orderItem = new OrderItem();
      orderItem.setOrderNumber(orderVo.getOrderSn());
      orderItem.setProdId(shopCartItem.getProdId());
      orderItem.setPic(shopCartItem.getPic());
      orderItem.setProdName(shopCartItem.getProdName());
      orderItem.setProdCount(shopCartItem.getBasketCount());
      orderItem.setPrice(shopCartItem.getPrice());
      orderItem.setSkuId(shopCartItem.getSkuId().intValue());
      orderItem.setSkuName(shopCartItem.getSkuName());
      orderItem.setProductTotalAmount(orderVo.getActualTotal());
      orderItem.setShopId(1L);
      orderItem.setRecTime(new Date());
      orderItemMapper.insert(orderItem);
      stringBuilder.append(shopCartItem.getProdName()+" ");
    }
    Order order = new Order();
    order.setOrderNumber(orderVo.getOrderSn());
    order.setUserId(orderVo.getUserAddr().getUserId());
    order.setActualTotal(orderVo.getActualTotal());
    order.setIsPayed(Boolean.FALSE);
    order.setProdName(stringBuilder.toString());
    order.setUpdateTime(new Date());
    order.setCreateTime(new Date());
    orderMapper.insert(order);

    OrderSettlement settlement = new OrderSettlement();
    settlement.setPayTypeName("支付宝");
    settlement.setPayType(0);
    settlement.setBizPayNo(orderVo.getOrderSn());
    settlement.setOrderNumber(orderVo.getOrderSn());
    settlement.setPayAmount(orderVo.getActualTotal());
    settlement.setIsClearing(0);
    settlement.setUserId(orderVo.getUserAddr().getUserId());
    orderSettlementMapper.insert(settlement);
  }

  private  Map<Long,Integer> decrStocks(OrderVo orderVo) {
    List<ShopCartItem> shopCartItems = orderVo.getShopCartOrders().get(0).getShopCartItemDiscounts().get(0).getShopCartItems();
    Map<Long,Integer> skuStocks = new HashMap<>();
    Map<Long,Integer> prodStocks = new HashMap<>();
    for (ShopCartItem shopCartItem : shopCartItems) {
      skuStocks.put(shopCartItem.getSkuId().longValue(),shopCartItem.getBasketCount());
      if (prodStocks.containsKey(shopCartItem.getProdId())){
        prodStocks.put(shopCartItem.getProdId(),prodStocks.get(shopCartItem.getProdId()+shopCartItem.getBasketCount()));
      }
      prodStocks.put(shopCartItem.getProdId(),shopCartItem.getBasketCount());
    }
   skuService.decrStocks(skuStocks);
    prodService.decrStocks(prodStocks);

    return  prodStocks;
  }

  /**
   * 通过订单的id 得到该订单对应的商品
   * @param orderNum
   * @return
   */
  private List<ShopCartItem> getCartItems(String orderNum) {
    List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
      .eq(OrderItem::getOrderNumber, orderNum).orderByDesc(OrderItem::getRecTime)
    );
    List<ShopCartItem> shopCartItems = null;
    if(orderItems!=null && !orderItems.isEmpty()){
      shopCartItems = new ArrayList<>(orderItems.size()) ;
      for (OrderItem orderItem : orderItems) {
        ShopCartItem shopCartItem = ShopCartItem.builder()
          .skuName(orderItem.getSkuName())
          .prodName(orderItem.getProdName())
          .prodId(orderItem.getProdId())
          .pic(orderItem.getPic())
          .price(orderItem.getPrice())
          .basketCount(orderItem.getProdCount()).build();
        shopCartItems.add(shopCartItem) ;
      }
    }
    return shopCartItems ;
  }

}

