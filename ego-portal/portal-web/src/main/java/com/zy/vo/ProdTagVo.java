package com.zy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProdTagVo {

  @ApiModelProperty("标签活动的id")
  private  Long id;

  @ApiModelProperty("标签活动的标题")
  private  String title;

  @ApiModelProperty("标签活动的样式")
  private  Integer style;

}
