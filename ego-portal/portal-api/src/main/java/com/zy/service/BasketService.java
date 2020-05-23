package com.zy.service;

import com.zy.entity.Basket;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.model.CartTotalMoney;
import com.zy.model.ShopCartItem;
import com.zy.model.ShopCartItemDiscount;

import java.util.List;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
public interface BasketService extends IService<Basket> {

  /**
   * 修改购物车的条目数据
   * @param basket
   */
  void changetItem(Basket basket);

  Integer getBasketTotalCount(String userId);

  List<ShopCartItemDiscount> getShopCartItemDiscounts(String userId);

  /**
   * 计算用户的总金额
   * @param basketIds
   * @return
   */
  CartTotalMoney getCartTotalMoney(List<Long> basketIds);

  /**
   * 利用购物车id查询购物车商品
   * @param basketIds
   * @return
   */
  List<ShopCartItem> getShopCartItems(List<Long> basketIds);

  void clearCart(String userId);
}
