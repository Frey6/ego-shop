package com.zy.model;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车里面的一个条目
 */
@Data
@Builder
public class ShopCartItem implements Serializable {

  @ApiModelProperty("购物车的id")
  private Long basketId ;

  @ApiModelProperty("该条目是否被选择")
  private Boolean checked ;

  @ApiModelProperty("该条目对应的商品id")
  private Long prodId ;

  @ApiModelProperty("该条目对应的商品的名称")
  private String prodName ;

  @ApiModelProperty("该条目SkuId")
  private Integer skuId  ;


  @ApiModelProperty("该条目的名称")
  private String skuName ;

  @ApiModelProperty("该条目对应的主图")
  private String pic ;


  @ApiModelProperty("该条目对应的价格")
  private BigDecimal price ;

  @ApiModelProperty("该条目对应数量")
  private Integer basketCount ;
}
