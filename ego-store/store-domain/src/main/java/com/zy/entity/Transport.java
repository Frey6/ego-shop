package com.zy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
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
@ApiModel(value="Transport对象", description="")
public class Transport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "运费模板id")
    @TableId(value = "transport_id", type = IdType.AUTO)
    private Long transportId;

    @ApiModelProperty(value = "运费模板名称")
    private String transName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "店铺id")
    private Long shopId;

    @ApiModelProperty(value = "收费方式（0 按件数,1 按重量 2 按体积）")
    private Integer chargeType;

    @ApiModelProperty(value = "是否包邮 0:不包邮 1:包邮")
    private Integer isFreeFee;

    @ApiModelProperty(value = "是否含有包邮条件 0 否 1是")
    private Integer hasFreeCondition;

    @TableField(exist = false)
    @ApiModelProperty("所有的收费模板")
    private List<Transfee> transfees= Collections.emptyList();

      @TableField(exist = false)
      @ApiModelProperty("所有免费的模板")
      private List<TransfeeFree> transfeeFrees= Collections.emptyList();


}
