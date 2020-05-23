package com.zy.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zy.model.ShopCartItem;
import com.zy.model.ShopCartItemDiscount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Order对象", description="订单表")
@TableName(value = "`order`")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    @ApiModelProperty(value = "店铺id")
    private Long shopId;

    @ApiModelProperty(value = "产品名称,多个产品将会以逗号隔开")
    private String prodName;

    @ApiModelProperty(value = "订购用户ID")
    private String userId;

    @ApiModelProperty(value = "订购流水号")
    private String orderNumber;

    @ApiModelProperty(value = "总值")
    private BigDecimal total;

    @ApiModelProperty(value = "实际总值")
    private BigDecimal actualTotal;

    @ApiModelProperty(value = "支付方式 0 手动代付 1 微信支付 2 支付宝")
    private Integer payType;

    @ApiModelProperty(value = "订单备注")
    private String remarks;

    @ApiModelProperty(value = "订单状态 1:待付款 2:待发货 3:待收货 4:待评价 5:成功 6:失败")
    private Integer status;

    @ApiModelProperty(value = "配送类型")
    private String dvyType;

    @ApiModelProperty(value = "配送方式ID")
    private Long dvyId;

    @ApiModelProperty(value = "物流单号")
    private String dvyFlowId;

    @ApiModelProperty(value = "订单运费")
    private BigDecimal freightAmount;

    @ApiModelProperty(value = "用户订单地址Id")
    private Long addrOrderId;

    @ApiModelProperty(value = "订单商品总数")
    private Integer productNums;

    @ApiModelProperty(value = "订购时间")
    private Date createTime;

    @ApiModelProperty(value = "订单更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "付款时间")
    private Date payTime;

    @ApiModelProperty(value = "发货时间")
    private Date dvyTime;

    @ApiModelProperty(value = "完成时间")
    private Date finallyTime;

    @ApiModelProperty(value = "取消时间")
    private Date cancelTime;

    @ApiModelProperty(value = "是否已经支付，1：已经支付过，0：，没有支付过")
    private Boolean isPayed;

    @ApiModelProperty(value = "用户订单删除状态，0：没有删除， 1：回收站， 2：永久删除")
    private Integer deleteStatus;

    @ApiModelProperty(value = "0:默认,1:在处理,2:处理完成")
    private Integer refundSts;

    @ApiModelProperty(value = "优惠总额")
    private BigDecimal reduceAmount;

    @ApiModelProperty(value = "订单类型")
    private Integer orderType;

    @ApiModelProperty(value = "订单关闭原因 1-超时未支付 2-退款关闭 4-买家取消 15-已通过货到付款交易")
    private byte closeType;

    @TableField(exist = false)
    private List<ShopCartItem> orderItemDtos= Collections.EMPTY_LIST;


}
