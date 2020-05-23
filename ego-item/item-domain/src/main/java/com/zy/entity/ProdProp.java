package com.zy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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
@ApiModel(value="ProdProp对象", description="")
public class ProdProp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "属性id")
    @TableId(value = "prop_id", type = IdType.AUTO)
    private Long propId;

    @NotBlank
    @ApiModelProperty(value = "属性名称")
    private String propName;

    @ApiModelProperty(value = "ProdPropRule 1:销售属性(规格); 2:参数属性;")
    private Integer rule;

    @ApiModelProperty(value = "店铺id")
    private Long shopId;

    @NotEmpty
    @TableField(exist = false)
    @ApiModelProperty(value = "该属性里面的属性")
    private List<ProdPropValue> prodPropValues;


}
