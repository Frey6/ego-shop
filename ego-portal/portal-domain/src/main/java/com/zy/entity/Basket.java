package com.zy.entity;

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
 * 购物车
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Basket对象", description="购物车")
public class Basket implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "basket_id", type = IdType.AUTO)
    private Long basketId;

    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @ApiModelProperty(value = "产品ID")
    private Long prodId;

    @NotNull
    @ApiModelProperty(value = "SkuID")
    private Integer skuId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "购物车产品个数")
    @NotNull
    private Integer basketCount;

    @ApiModelProperty(value = "购物时间")
    private Date basketDate;

    @ApiModelProperty(value = "满减活动ID")
    private Long discountId;

    @ApiModelProperty(value = "分销推广人卡号")
    private String distributionCardNo;


}
