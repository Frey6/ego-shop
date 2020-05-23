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
@ApiModel(value="TranscityFree对象", description="")
public class TranscityFree implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "指定条件包邮城市项id")
    @TableId(value = "transcity_free_id", type = IdType.AUTO)
    private Long transcityFreeId;

    @ApiModelProperty(value = "指定条件包邮项id")
    private Long transfeeFreeId;

    @ApiModelProperty(value = "城市id")
    private Long freeCityId;


}
