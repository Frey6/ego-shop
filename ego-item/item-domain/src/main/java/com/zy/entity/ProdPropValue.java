package com.zy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value="ProdPropValue对象", description="")
public class ProdPropValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "属性值ID")
    @TableId(value = "value_id", type = IdType.AUTO)
    private Long valueId;

    @ApiModelProperty(value = "属性值名称")
    private String propValue;

    @ApiModelProperty(value = "属性ID")
    private Long propId;


}
