package com.zy.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zy.entity.Basket;
import com.zy.entity.Prod;
import com.zy.entity.Sku;
import com.zy.mapper.BasketMapper;
import com.zy.model.CartTotalMoney;
import com.zy.model.ShopCartItem;
import com.zy.model.ShopCartItemDiscount;
import com.zy.service.BasketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.service.ProdService;
import com.zy.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Slf4j
@Service(timeout = 10000,retries = 0)
public class BasketServiceImpl extends ServiceImpl<BasketMapper, Basket> implements BasketService{

  @Autowired
  private BasketMapper basketMapper ;

  @Reference(check = false)
  private SkuService skuService;

  @Reference(check = false)
  private ProdService prodService;

  @Override
  public void changetItem(Basket basket) {
    log.info("添加到购物的数据为{}", JSONUtil.toJsonPrettyStr(basket));
    basket.setBasketDate(new Date());

    // user_id 和 sku_id 可以决定一个唯一的值
    Basket dbBasket = basketMapper.selectOne(new LambdaQueryWrapper<Basket>()
      .eq(Basket::getShopId, 1)
      .eq(Basket::getUserId, basket.getUserId()) // 用户的id
      .eq(Basket::getSkuId, basket.getSkuId()));// 商品的skuid
    // 1 之前用户的购物车里面没有该sku ，则新增一个
    if(dbBasket==null){ // 代表之前没有
      basketMapper.insert(basket) ;
    }else{  // 2 之前用户的购物车里面有该sku ，怎修改数量就可以了
      @NotNull Integer basketCount = basket.getBasketCount();
      Integer basketNum = dbBasket.getBasketCount() + basketCount ; // 别人的值，可能时负数
      if(basketNum<=0){
        throw  new IllegalArgumentException("购物车的数据不合法") ;
      }
      dbBasket.setBasketCount(basketNum);
      basketMapper.updateById(dbBasket) ;
    }


  }

  @Override
  public Integer getBasketTotalCount(String userId) {
    log.info("当前用户{}购物车的总条数",userId);
    List<Object> totalCount = basketMapper.selectObjs(new QueryWrapper< Basket>().
      select("SUM(basket_count)").
      eq("user_id", userId));
    Integer count = Integer.valueOf(totalCount.get(0).toString());
    return count;
  }


  /**
   * 根据用户的id 查询用户的购物车的数据详情
   * @param userId
   * @return
   */
  @Override
  public List<ShopCartItemDiscount> getShopCartItemDiscounts(String userId) {
    List<Basket> baskets = basketMapper.selectList(new LambdaQueryWrapper<Basket>()
      .eq(Basket::getUserId, userId)
      .orderByDesc(Basket::getBasketDate));
    // 返回值为：
    List<ShopCartItemDiscount> shopCartItemDiscounts = new ArrayList<>(1) ;
    ShopCartItemDiscount shopCartItemDiscount = new ShopCartItemDiscount();
//    new ShopCartItemDiscount();
    shopCartItemDiscounts.add(shopCartItemDiscount) ;


    // 查询购物车的条目数据
    if(baskets!=null && !baskets.isEmpty()){
      List<ShopCartItem> shopCartItems = new ArrayList<>(baskets.size());
      for (Basket basket : baskets) {
        Sku sku = skuService.getById(basket.getSkuId());
        Prod prod = prodService.getSimpleProd(basket.getProdId());
        ShopCartItem shopCartItem = ShopCartItem.builder()
          .basketId(basket.getBasketId())
          .prodId(basket.getProdId())
          .skuId(basket.getSkuId())
          .basketCount(basket.getBasketCount())
          .checked(Boolean.TRUE)
          .pic(StringUtils.hasText(sku.getPic())?sku.getPic():prod.getPic())
          .price(sku.getPrice())
          .prodName(prod.getProdName()) // 此时我们在Service 里面无法远程调用
          .skuName(sku.getSkuName())
          .build();
        shopCartItems.add(shopCartItem) ;
      }
      shopCartItemDiscount.setShopCartItems(shopCartItems);
    }
    return shopCartItemDiscounts;
  }

  @Override
  public CartTotalMoney getCartTotalMoney(List<Long> basketIds) {
    // 只有有sku的id就能计算总的金额
    List<Basket> baskets = basketMapper.selectList(new LambdaQueryWrapper<Basket>().
      in(Basket::getBasketId, basketIds));
    int totalCount=0;
    BigDecimal totalMoney = BigDecimal.ZERO;
    BigDecimal subtractMoney = BigDecimal.ZERO;
    if(baskets!=null && !baskets.isEmpty()){
      for (Basket basket : baskets) {
        @NotNull Integer skuId = basket.getSkuId();
        Sku sku = skuService.getById(skuId);
        @NotNull Integer basketCount = basket.getBasketCount();
        totalMoney =  totalMoney.add(sku.getPrice().multiply(BigDecimal.valueOf(basketCount))) ;
        totalCount+=basket.getBasketCount();
      }
    }

    CartTotalMoney cartTotalMoney = CartTotalMoney.builder()
      .prodTotalCount(totalCount)
      .totalMoney(totalMoney.setScale(2,BigDecimal.ROUND_HALF_UP))
      .subtractMoney(subtractMoney).build();
    return cartTotalMoney;
  }

  @Override
  public List<ShopCartItem> getShopCartItems(List<Long> basketIds) {
    List<Basket> baskets = basketMapper.selectList(new LambdaQueryWrapper<Basket>()
      .in(Basket::getBasketId, basketIds)
      .orderByDesc(Basket::getBasketDate));

    List<ShopCartItem> shopCartItems=null;
    // 查询购物车的条目数据
    if(baskets!=null && !baskets.isEmpty()){
      shopCartItems = new ArrayList<>(baskets.size());
      for (Basket basket : baskets) {
        Sku sku = skuService.getById(basket.getSkuId());
        Prod prod = prodService.getSimpleProd(basket.getProdId());
        ShopCartItem shopCartItem = ShopCartItem.builder()
          .basketId(basket.getBasketId())
          .prodId(basket.getProdId())
          .skuId(basket.getSkuId())
          .basketCount(basket.getBasketCount())
          .checked(Boolean.TRUE)
          .pic(StringUtils.hasText(sku.getPic())?sku.getPic():prod.getPic())
          .price(sku.getPrice())
          .prodName(prod.getProdName()) // 此时我们在Service 里面无法远程调用
          .skuName(sku.getSkuName())
          .build();
        shopCartItems.add(shopCartItem) ;
      }

    }
    return shopCartItems;
  }

  @Override
  public void clearCart(String userId) {
    int delete = basketMapper.delete(new LambdaQueryWrapper<Basket>()
      .eq(Basket::getUserId, userId));
  }


}

