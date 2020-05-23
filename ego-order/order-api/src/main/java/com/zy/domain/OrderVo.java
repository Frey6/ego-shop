package com.zy.domain;

import com.zy.entity.UserAddr;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Builder
@Data
public class OrderVo implements Serializable {
  @ApiModelProperty("订单里面的每个条目")
  private List<ShopCartOrder> shopCartOrders= Collections.emptyList();

  @ApiModelProperty("订单的实际金额")
  private BigDecimal actualTotal;

  @ApiModelProperty("订单的总金额")
  private BigDecimal total;

  @ApiModelProperty("订单里面总商品数量")
  private Integer totalCount;

  @ApiModelProperty("订单的地址")
  private UserAddr userAddr;


  private  String orderSn;

}
