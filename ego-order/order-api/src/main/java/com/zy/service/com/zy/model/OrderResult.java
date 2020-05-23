package com.zy.service.com.zy.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderResult implements Serializable {

  @ApiModelProperty("未付款")
  private Integer unPay=0;
  @ApiModelProperty("待发货的")
  private Integer payed=0;
  @ApiModelProperty("待收货的")
  private Integer consignment = 0;
}
