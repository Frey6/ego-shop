package com.zy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 评论数据的vo 对象
 */
@Data
public class ProdCommVo {

  @ApiModelProperty("评论的id")
  private Long prodCommId ;


  @ApiModelProperty(value="评论用户ID")
  private String userId;

  @ApiModelProperty("评论的图片")
  private String pics ;

  @ApiModelProperty("评论的记录数据")
  private Date recTime ;


  @ApiModelProperty("用户的头像")
  private String pic ;

  @ApiModelProperty("用户的昵称")
  private String nickName ;

  @ApiModelProperty("评论的内容")
  private String content ;

  @ApiModelProperty("用户的评分")
  private Byte score ;
}
