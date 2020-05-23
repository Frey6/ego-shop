package com.zy.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="User对象", description="用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String userId;

    @NotBlank
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "用户邮箱")
    private String userMail;

    @ApiModelProperty(value = "登录密码")
    private String loginPassword;

    @ApiModelProperty(value = "支付密码")
    private String payPassword;

    @ApiModelProperty(value = "手机号码")
    private String userMobile;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;

    @ApiModelProperty(value = "注册时间")
    private Date userRegtime;

    @ApiModelProperty(value = "注册IP")
    private String userRegip;

    @ApiModelProperty(value = "最后登录时间")
    private Date userLasttime;

    @ApiModelProperty(value = "最后登录IP")
    private String userLastip;

    @ApiModelProperty(value = "备注")
    private String userMemo;

    @ApiModelProperty(value = "M(男) or F(女)")
    private String sex;

    @ApiModelProperty(value = "例如：2009-11-27")
    private String birthDate;

    @NotBlank
    @ApiModelProperty(value = "头像图片路径")
    private String pic;

    @ApiModelProperty(value = "状态 1 正常 0 无效")
    private Integer status;

    @ApiModelProperty(value = "用户积分")
    private Integer score;


}
