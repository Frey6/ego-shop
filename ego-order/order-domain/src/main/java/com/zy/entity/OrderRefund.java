package com.zy.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderRefund对象", description="")
public class OrderRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "refund_id", type = IdType.AUTO)
    private Long refundId;

    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "订单流水号")
    private String orderNumber;

    @ApiModelProperty(value = "订单总金额")
    private Double orderAmount;

    @ApiModelProperty(value = "订单项ID 全部退款是0")
    private Long orderItemId;

    @ApiModelProperty(value = "退款编号")
    private String refundSn;

    @ApiModelProperty(value = "订单支付流水号")
    private String flowTradeNo;

    @ApiModelProperty(value = "第三方退款单号(微信退款单号)")
    private String outRefundNo;

    @ApiModelProperty(value = "订单支付方式 1 微信支付 2 支付宝")
    private Integer payType;

    @ApiModelProperty(value = "订单支付名称")
    private String payTypeName;

    @ApiModelProperty(value = "买家ID")
    private String userId;

    @ApiModelProperty(value = "退货数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty(value = "申请类型:1,仅退款,2退款退货")
    private Integer applyType;

    @ApiModelProperty(value = "处理状态:1为待审核,2为同意,3为不同意")
    private Integer refundSts;

    @ApiModelProperty(value = "处理退款状态: 0:退款处理中 1:退款成功 -1:退款失败")
    private Integer returnMoneySts;

    @ApiModelProperty(value = "申请时间")
    private LocalDateTime applyTime;

    @ApiModelProperty(value = "卖家处理时间")
    private LocalDateTime handelTime;

    @ApiModelProperty(value = "退款时间")
    private LocalDateTime refundTime;

    @ApiModelProperty(value = "文件凭证json")
    private String photoFiles;

    @ApiModelProperty(value = "申请原因")
    private String buyerMsg;

    @ApiModelProperty(value = "卖家备注")
    private String sellerMsg;

    @ApiModelProperty(value = "物流公司名称")
    private String expressName;

    @ApiModelProperty(value = "物流单号")
    private String expressNo;

    @ApiModelProperty(value = "发货时间")
    private LocalDateTime shipTime;

    @ApiModelProperty(value = "收货时间")
    private LocalDateTime receiveTime;

    @ApiModelProperty(value = "收货备注")
    private String receiveMessage;


}
