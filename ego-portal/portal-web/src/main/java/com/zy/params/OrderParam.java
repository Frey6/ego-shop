package com.zy.params;

import com.zy.model.ShopCartItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderParam {
  @ApiModelProperty("订单的收货地址的id")
  private Integer addrId;
  @ApiModelProperty("要购买的购物车条目的id，用户从购物车页面来")
  private List<Long> basketIds;
  @ApiModelProperty("购买商品的条目，从页面详情来")
  private ShopCartItem orderItem;
  private List<Integer> couponIds;
  private Integer userChangeCoupon;
}
