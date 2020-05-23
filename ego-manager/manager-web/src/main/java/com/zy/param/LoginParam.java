package com.zy.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginParam {
    @NotBlank(message = "登录用户名不能为null")
    @ApiModelProperty(value = "用户名")
    public String  principal;

    @NotBlank()
    @ApiModelProperty(value = "密码")
    public  String credentials;

    @NotBlank()
    @ApiModelProperty(value = "前端的uuid")
    public  String sessionUUID;

    @NotBlank()
    @ApiModelProperty(value = "验证码")
    public  String imageCode;

}
