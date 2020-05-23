package com.zy.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CategoryVo {
  @ApiModelProperty("分类的id")
  private  Long categoryId;

  @ApiModelProperty("分类的名称")
  private  String categoryName;

  @ApiModelProperty("分类的名称")
  private  String pic;

}
