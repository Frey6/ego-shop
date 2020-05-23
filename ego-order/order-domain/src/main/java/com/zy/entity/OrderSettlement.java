package com.zy.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
@ApiModel(value="OrderSettlement对象", description="")
public class OrderSettlement implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "支付结算单据ID")
    @TableId(value = "settlement_id", type = IdType.AUTO)
    private Long settlementId;

    @ApiModelProperty(value = "支付单号")
    private String payNo;

    @ApiModelProperty(value = "外部订单流水号")
    private String bizPayNo;

    @NotBlank
    @ApiModelProperty(value = "order表中的订单号")
    private String orderNumber;

    @NotNull
    @ApiModelProperty(value = "支付方式 1 微信支付 2 支付宝")
    private Integer payType;

    @ApiModelProperty(value = "支付方式名称")
    private String payTypeName;

    @ApiModelProperty(value = "支付金额")
//    @NotBlank
    private BigDecimal payAmount;

    @ApiModelProperty(value = "是否清算 0:否 1:是")
    private Integer isClearing;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "清算时间")
    private Date clearingTime;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "支付状态")
    private Integer payStatus;


}
