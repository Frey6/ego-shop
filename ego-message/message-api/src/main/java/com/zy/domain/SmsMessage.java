package com.zy.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SmsMessage implements Serializable {

  private String regionId = "cn-hangzhou";
  @ApiModelProperty("发送的手机号")
  private String phoneNumbers;
  @ApiModelProperty("发送短信的签名")
  private String signName;
  @ApiModelProperty("发送短信的模板")
  private String templateCode;
  @ApiModelProperty("发送短信模板的数据")
  private String templateParam;
  @ApiModelProperty("发送短信的扩展的数据")
  private String smsUpExtendCode;
  @ApiModelProperty("外呼人")
  private String outId;
}
