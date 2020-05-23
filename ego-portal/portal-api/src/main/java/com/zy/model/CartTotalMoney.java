package com.zy.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartTotalMoney {

  @ApiModelProperty("最终金额")
  private BigDecimal finalMoney ;

  @ApiModelProperty("总的金额")
  private BigDecimal totalMoney ;

  @ApiModelProperty("满减金额")
  private BigDecimal subtractMoney ;

  @ApiModelProperty("总的商品数量")
  private Integer prodTotalCount;



  public BigDecimal getFinalMoney() {
    if (totalMoney == null) {
      return new BigDecimal("0.00");
    }
    if (subtractMoney == null) {
      return totalMoney;
    }
    return totalMoney.add(BigDecimal.valueOf(-1).multiply(subtractMoney)).setScale(2, BigDecimal.ROUND_UP);
  }


}
