package com.zy.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Builder
@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class ShopCartOrder implements Serializable {
  @ApiModelProperty("订单里面的每一个商品")
  private List<ShopCartItemDiscount> shopCartItemDiscounts= Collections.emptyList();

  @ApiModelProperty("订单的运费")
  private BigDecimal transfee;

  @ApiModelProperty("订单的优惠")
  private BigDecimal shopReduce;
}
