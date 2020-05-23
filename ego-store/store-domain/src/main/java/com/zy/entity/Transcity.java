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
@ApiModel(value="Transcity对象", description="")
public class Transcity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "transcity_id", type = IdType.AUTO)
    private Long transcityId;

    @ApiModelProperty(value = "运费项id")
    private Long transfeeId;

    @ApiModelProperty(value = "城市id")
    private Long cityId;


}
