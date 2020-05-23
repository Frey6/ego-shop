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
 * 商品收藏表
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProdFavorite对象", description="商品收藏表")
public class ProdFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "favorite_id", type = IdType.AUTO)
    private Long favoriteId;

    @ApiModelProperty(value = "商品ID")
    private Long prodId;

    @ApiModelProperty(value = "收藏时间")
    private LocalDateTime recTime;

    @ApiModelProperty(value = "用户ID")
    private String userId;


}
