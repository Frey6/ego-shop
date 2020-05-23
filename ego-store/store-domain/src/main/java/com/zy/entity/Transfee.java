package com.zy.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

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
@ApiModel(value="Transfee对象", description="")
public class Transfee implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "运费项id")
    @TableId(value = "transfee_id", type = IdType.AUTO)
    private Long transfeeId;

    @ApiModelProperty(value = "运费模板id")
    private Long transportId;

    @ApiModelProperty(value = "续件数量")
    private BigDecimal continuousPiece;

    @ApiModelProperty(value = "首件数量")
    private BigDecimal firstPiece;

    @ApiModelProperty(value = "续件费用")
    private BigDecimal continuousFee;

    @ApiModelProperty(value = "首件费用")
    private BigDecimal firstFee;

    @TableField(exist = false)
    @ApiModelProperty("收费的城市的中间表")
    private List<Area> cityList= Collections.emptyList();

}
