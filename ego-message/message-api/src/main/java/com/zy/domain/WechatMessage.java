package com.zy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class WechatMessage implements Serializable {
  @ApiModelProperty("发送给那个用户")
  @JsonProperty("touser")
  private String toUser;
  @ApiModelProperty("本次发消息的模板是哪一个")
  @JsonProperty("template_id")
  private String templateId;
  @ApiModelProperty("点击该消息跳转到那个页面")
  private String url;
  @ApiModelProperty("微信消息的顶层颜色")
  @JsonProperty("topcolor")
  private String topColor;
  @ApiModelProperty("模板消息数据")
  private Map<String, Map<String, String>> data;

  public static Map<String, String> buildProp(String value, String color) {
    HashMap<String, String> prop = new HashMap<>(2);
     prop.put("value",value);
     prop.put("color",color);
     return  prop;
  }
}
