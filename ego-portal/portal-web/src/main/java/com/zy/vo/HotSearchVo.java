package com.zy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HotSearchVo {
  @ApiModelProperty("热搜的标题")
  private String title;
  @ApiModelProperty("热搜的内容")
  private String content;

}
