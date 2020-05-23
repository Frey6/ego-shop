package com.zy.vo;

import com.zy.model.ShopCartItemDiscount;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class BasketResult {
  @ApiModelProperty("购物车条目里面的数据")
  private List<ShopCartItemDiscount> shopCartItemDiscounts = Collections.emptyList();
}
