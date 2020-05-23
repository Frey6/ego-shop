package com.zy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NoticeVo {
  @ApiModelProperty("公告的标题")
  private  String title;
}
