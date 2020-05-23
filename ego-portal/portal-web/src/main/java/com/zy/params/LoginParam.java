package com.zy.params;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginParam
{
  @ApiModelProperty("验证码")
  private String principal;




}
