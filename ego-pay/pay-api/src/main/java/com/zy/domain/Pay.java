package com.zy.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 这是一个参数的对象
 */
/**
 * 这是一个参数的对象
 */

@Data
@JSONType(ignores = {"payType"})
public class Pay  implements Serializable {

  @ApiModelProperty("支付的类型,0:扫描支付;1:网页的支付")
  private Integer payType ;
  // 需保证商户系统端不能重复，建议通过数据库sequence生成，

  @JSONField(name = "out_trade_no")
  @ApiModelProperty("订单号")
  private String outTradeNo ;

  // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
  @ApiModelProperty("订单的标题")
  private  String subject ;

  // (必填) 订单总金额，单位为元，不能超过1亿元
  // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
  @JSONField(name = "total_amount")
  @ApiModelProperty("订单的总金额")
  private String totalAmount ;

  // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
  // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
  @JSONField(name = "undiscountable_amount")
  @ApiModelProperty("订单的折扣金额")
  private String undiscountableAmount = "0";

  // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
  // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
  @JSONField(name = "seller_id")
  private String sellerId = "";

  // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
  @ApiModelProperty("订单描述")
  private String body ;

  // 商户操作员编号，添加此参数可以为商户操作员做销售统计
  @ApiModelProperty("操作员的编号,测试缓存是写死的")
  @JSONField(name = "operator_id")
  private String operatorId = "test_operator_id";

  // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
  @ApiModelProperty("门店的编号,测试缓存是写死的")
  @JSONField(name = "store_id")
  private String storeId = "test_store_id";

  // 支付超时，定义为120分钟
  @ApiModelProperty("支付的超时时间")
  @JSONField(name = "timeout_express")
  private String timeoutExpress = "120m";


  @ApiModelProperty("产品码")
  @JSONField(name = "product_code")
  private String productCode = "FAST_INSTANT_TRADE_PAY" ;

}
