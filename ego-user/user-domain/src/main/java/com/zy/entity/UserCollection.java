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
@ApiModel(value="UserCollection对象", description="")
public class UserCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "收藏表")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商品id")
    private Long prodId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "收藏时间")
    private Date createTime;


}
