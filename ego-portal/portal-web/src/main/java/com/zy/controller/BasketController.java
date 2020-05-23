package com.zy.controller;


import com.zy.entity.Basket;
import com.zy.entity.Prod;
import com.zy.entity.Sku;
import com.zy.entity.User;
import com.zy.model.CartTotalMoney;
import com.zy.model.ShopCartItem;
import com.zy.model.ShopCartItemDiscount;
import com.zy.service.BasketService;
import com.zy.service.ProdService;
import com.zy.service.SkuService;
import com.zy.vo.BasketResult;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BasketController
{
  @Autowired
  private BasketService basketService;
  @Reference(check=false)
  private SkuService skuService;
  @Reference(check=false)
  private ProdService prodService;

  @PostMapping({"/p/shopCart/changeItem"})
  @ApiOperation("修改购物车的数量")
  public ResponseEntity<Void> changeItem(@RequestBody @Validated Basket basket)
  {
    User user = (User) SecurityUtils.getSubject().getPrincipal();
    basket.setUserId(user.getUserId());
    this.basketService.changetItem(basket);
    return ResponseEntity.ok().build();
  }

  @GetMapping({"p/shopCart/prodCount"})
  @ApiOperation("获取当前用户的总条数")
  public ResponseEntity<Integer> getBasketTotalCount()
  {
    User user = (User)SecurityUtils.getSubject().getPrincipal();
    Integer totalCount = this.basketService.getBasketTotalCount(user.getUserId());
    return ResponseEntity.ok(totalCount);
  }

  @PostMapping("/p/shopCart/info")
  @ApiOperation("回显购物车的详情")
  public ResponseEntity<List<BasketResult>> basketResultInfo(){
    User user = (User)SecurityUtils.getSubject().getPrincipal();
    List<ShopCartItemDiscount> shopCartItemDiscouts = this.basketService.getShopCartItemDiscounts(user.getUserId());
    List<BasketResult> resultArrayList = new ArrayList(1);
    BasketResult basketResult = new BasketResult();
    basketResult.setShopCartItemDiscounts(shopCartItemDiscouts);
    resultArrayList.add(basketResult);
    return ResponseEntity.ok(resultArrayList);
  }

  @PostMapping("/p/shopCart/totalPay")
  @ApiModelProperty("计算总的金额")
  public ResponseEntity<CartTotalMoney> getTotalMoney(@RequestBody  List<Long> basketIds){
    if(basketIds==null || basketIds.isEmpty()){
      CartTotalMoney cartTotalMoney = CartTotalMoney.builder().
        subtractMoney(new BigDecimal("0.00")).
        totalMoney(new BigDecimal("0.00")).build();
      return ResponseEntity.ok(cartTotalMoney) ;
    }
    CartTotalMoney cartTotalMoney = basketService.getCartTotalMoney(basketIds);
    return ResponseEntity.ok(cartTotalMoney) ;
  }



//  @PostMapping({"/p/shopCart/totalPay"})
//  @ApiModelProperty("������������")
//  public ResponseEntity<CartTotalMoney> getTotalMoney(@RequestBody List<Long> basketIds)
//  {
//    if ((basketIds == null) || (basketIds.isEmpty()))
//    {
//      CartTotalMoney cartTotalMoney = CartTotalMoney.builder().subtractMoney(new BigDecimal("0.00")).totalMoney(new BigDecimal("0.00")).build();
//      return ResponseEntity.ok(cartTotalMoney);
//    }
//    CartTotalMoney cartTotalMoney = this.basketService.getCartTotalMoney(basketIds);
//    return ResponseEntity.ok(cartTotalMoney);
//  }
//
  @DeleteMapping({"/p/shopCart/deleteItem"})
  public ResponseEntity<Void> deleteItem(@RequestBody List<Long> basketIds)
  {
    this.basketService.removeByIds(basketIds);
    return ResponseEntity.ok().build();
  }
}

