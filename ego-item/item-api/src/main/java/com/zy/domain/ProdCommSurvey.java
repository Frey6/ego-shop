package com.zy.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品的评率的预览
 */
/**
 * 商品的评率的预览
 */
@Data
public class ProdCommSurvey implements Serializable {

  @ApiModelProperty("商品的id")
  private Long prodId ;

  @ApiModelProperty("商品的好评数")
  private Long praiseNumber ;

  @ApiModelProperty("商品的好评率")
  private BigDecimal positiveRating ;

  @ApiModelProperty("总的评论数")
  private Long number ;

  @ApiModelProperty("总的评论数")
  private Long secondaryNumber ;

  @ApiModelProperty("有图的评论")
  private Long picNumber ;

  @ApiModelProperty("差评的数")
  private Long negativeNumber ;

  public ProdCommSurvey(Long praiseNumber ,Long number){
    this.praiseNumber = praiseNumber ;
    this.number = number ;
  }

  public BigDecimal getPositiveRating(){
    if(number==null || number.equals(0L)){
      this.positiveRating = new BigDecimal("0.00") ;
      return this.positiveRating ;
    }
    if(this.praiseNumber==null || this.praiseNumber.equals(0L)){
      this.positiveRating = new BigDecimal("0.00") ;
      return this.positiveRating ;
    }
    this.positiveRating =  new BigDecimal(praiseNumber).divide(new BigDecimal(number)).multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_DOWN);
    return  this.positiveRating ;
  }
}
