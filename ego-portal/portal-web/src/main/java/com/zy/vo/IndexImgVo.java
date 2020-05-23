package com.zy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IndexImgVo
{
  @ApiModelProperty("轮播图的地址")
  private String imgUrl;
  @ApiModelProperty("广告对应的商品")
  private Long relation;


}

