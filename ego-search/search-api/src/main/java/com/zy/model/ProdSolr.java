package com.zy.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProdSolr implements Serializable {

  @ApiModelProperty("商品的id")
  private  Long prodId ;

  @ApiModelProperty("商品的名称")
  private String prodName ;

  @ApiModelProperty("商品的价格")
  private BigDecimal price ;

  @ApiModelProperty("商品的销量")
  private Long soldNum ;

  @ApiModelProperty("商品的买点")
  private String brief ;

  @ApiModelProperty("商品的主图")
  private String pic ;

  @ApiModelProperty("商品的状态")
  private Integer status ;

  @ApiModelProperty("商品的库存")
  private Long totalStocks ;

  @ApiModelProperty("商品的分类id")
  private Long categoryId ;

  @ApiModelProperty("商品的标签")
  private List<Long> tagList ;

  @ApiModelProperty("商品的好评数")
  private Long praiseNumber ;

  @ApiModelProperty("商品的好评率")
  private BigDecimal positiveRating ;
}

