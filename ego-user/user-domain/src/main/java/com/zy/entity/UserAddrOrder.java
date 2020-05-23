package com.zy.entity;

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
 * 用户订单配送地址
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserAddrOrder对象", description="用户订单配送地址")
public class UserAddrOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "addr_order_id", type = IdType.AUTO)
    private Long addrOrderId;

    @ApiModelProperty(value = "地址ID")
    private Long addrId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "收货人")
    private String receiver;

    @ApiModelProperty(value = "省ID")
    private Long provinceId;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "区域ID")
    private Long areaId;

    @ApiModelProperty(value = "区")
    private String area;

    @ApiModelProperty(value = "城市ID")
    private Long cityId;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "地址")
    private String addr;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    @ApiModelProperty(value = "手机")
    private String mobile;

    @ApiModelProperty(value = "建立时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "版本号")
    private Integer version;


}
