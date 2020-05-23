package com.zy.controller;


import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.domain.OrderVo;
import com.zy.domain.ShopCartItemDiscount;
import com.zy.domain.ShopCartOrder;
import com.zy.entity.Order;
import com.zy.entity.Prod;
import com.zy.entity.Sku;
import com.zy.entity.User;
import com.zy.entity.UserAddr;
import com.zy.model.CartTotalMoney;
import com.zy.model.ShopCartItem;
import com.zy.params.OrderParam;
import com.zy.service.BasketService;
import com.zy.service.OrderService;
import com.zy.service.ProdService;
import com.zy.service.SkuService;
import com.zy.service.UserAddrService;
import com.zy.service.com.zy.model.OrderResult;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController
{
  @Reference(check=false)
  private OrderService orderService;
  @Reference(check=false)
  private UserAddrService userAddrService;
  @Autowired
  private BasketService basketService;
  @Reference(check=false)
  private ProdService prodService;
//  @Autowired
//  private Snowflake snowflake;
  @Reference(check=false)
  private SkuService skuService;
  @Autowired
  private RedisTemplate redisTemplate;
  private static final String ORDER_PREFIX = "user:pre:order:";

  @PostConstruct
  public void setRedisTemplate()
  {
    this.redisTemplate.setKeySerializer(new StringRedisSerializer());
  }

  @GetMapping({"/p/myOrder/orderCount"})
  @ApiModelProperty("返回当前登录对象的order预览")
  public ResponseEntity<OrderResult> getOrderCount()
  {
    User user = (User)SecurityUtils.getSubject().getPrincipal();
    OrderResult orderResult = this.orderService.getOrderCount(user.getUserId());
    return ResponseEntity.ok(orderResult);
  }

  @GetMapping({"/p/myOrder/myOrder"})
  @ApiOperation("查询用户的订单")
  public ResponseEntity<IPage<Order>> findByPage(Page<Order> page, Order order)
  {
    User user = (User)SecurityUtils.getSubject().getPrincipal();
    order.setUserId(user.getUserId());
    IPage<Order> pageDate = this.orderService.findByPage(page, order);
    return ResponseEntity.ok(pageDate);
  }
//
  @PostMapping({"/p/order/confirm"})
  @ApiModelProperty("确定下单")
  public ResponseEntity<OrderVo> confirmOrder(@RequestBody OrderParam orderParam)
  {
    List<ShopCartOrder> shopCartOrders = null;

    User user = (User)SecurityUtils.getSubject().getPrincipal();
    UserAddr userAddr = this.userAddrService.getUserDefaultAddr(user.getUserId());

    BigDecimal actualTotal = BigDecimal.ZERO;
    BigDecimal total = BigDecimal.ZERO;
    Integer totalCount = Integer.valueOf(0);
    List<Long> basketIds = orderParam.getBasketIds();
    if ((basketIds != null) && (!basketIds.isEmpty()))
    {
      shopCartOrders = buildShopCartItems(basketIds);
      CartTotalMoney cartTotalMoney = this.basketService.getCartTotalMoney(basketIds);
      actualTotal = cartTotalMoney.getFinalMoney();
      total = actualTotal.add(((ShopCartOrder)shopCartOrders.get(0)).getTransfee());
      totalCount = cartTotalMoney.getProdTotalCount();
    }
    else
    {
      ShopCartItem orderItem = orderParam.getOrderItem();
      if (orderItem != null)
      {
        shopCartOrders = buildShopCartItems(orderItem);
        actualTotal = orderItem.getPrice();
        total = actualTotal.add(((ShopCartOrder)shopCartOrders.get(0)).getTransfee());
        totalCount = Integer.valueOf(1);
      }
    }
    OrderVo orderVo = OrderVo.builder().actualTotal(actualTotal).total(total).totalCount(totalCount).shopCartOrders(shopCartOrders).userAddr(userAddr).build();
    this.redisTemplate.opsForValue().set("user:pre:order:" + user.getUserId(), orderVo);
    return ResponseEntity.ok(orderVo);
  }

  private List<ShopCartOrder> buildShopCartItems(List<Long> basketIds)
  {
    return getShopCartOrder(null, basketIds);
  }

  private List<ShopCartOrder> buildShopCartItems(ShopCartItem orderItem)
  {
    return getShopCartOrder(orderItem, null);
  }

  public List<ShopCartOrder> getShopCartOrder(ShopCartItem orderItem, List<Long> basketIds)
  {
    List<ShopCartItem> shopCartItems = null;
    if (orderItem != null)
    {
      shopCartItems = new ArrayList(1);

      Sku sku = (Sku)this.skuService.getById(orderItem.getSkuId());
      Prod simpleProd = this.prodService.getSimpleProd(orderItem.getProdId());
      orderItem.setPrice(sku.getPrice());

      ShopCartItem shopCartItem = ShopCartItem.builder().prodId(orderItem.getProdId()).basketCount(orderItem.getBasketCount()).price(sku.getPrice()).prodName(simpleProd.getProdName()).skuName(sku.getSkuName()).pic(StringUtils.hasText(sku.getPic()) ? sku.getPic() : simpleProd.getPic()).checked(Boolean.TRUE).skuId(orderItem.getSkuId()).build();
      shopCartItems.add(shopCartItem);
    }
    else
    {
      shopCartItems = this.basketService.getShopCartItems(basketIds);
    }
    List<ShopCartItemDiscount> shopCartItemDiscounts = new ArrayList(1);

    ShopCartItemDiscount shopCartItemDiscount = ShopCartItemDiscount.builder().shopCartItems(shopCartItems).build();
    shopCartItemDiscounts.add(shopCartItemDiscount);
    List<ShopCartOrder> shopCartOrders = new ArrayList(1);

    ShopCartOrder shopCartOrder = ShopCartOrder.builder().transfee(new BigDecimal("0.00")).shopReduce(new BigDecimal("0.00")).shopCartItemDiscounts(shopCartItemDiscounts).build();
    shopCartOrders.add(shopCartOrder);
    return shopCartOrders;
  }

  @PostMapping({"p/order/submit"})
  @ApiOperation("完成订单支付的功能")
  public ResponseEntity<String> toPay()
  {
    User user = (User) SecurityUtils.getSubject().getPrincipal();
//    String orderNums = this.snowflake.nextIdStr();
    String orderNums = new Date().getTime() + "";
    orderNums = orderNums.substring(0, 8);
    OrderVo orderVo = (OrderVo)this.redisTemplate.opsForValue().get("user:pre:order:" + user.getUserId());

    orderVo.setOrderSn(orderNums);
   this.orderService.toOrderPay(orderVo);
    return ResponseEntity.ok(orderNums);
  }
}

