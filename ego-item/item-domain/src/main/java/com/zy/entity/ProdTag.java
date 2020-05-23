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
 * 商品分组表
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProdTag对象", description="商品分组表")
public class ProdTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分组标签id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "分组标题")
    private String title;

    @ApiModelProperty(value = "店铺Id")
    private Long shopId;

    @ApiModelProperty(value = "状态(1为正常,0为删除)")
    private Boolean status;

    @ApiModelProperty(value = "默认类型(0:商家自定义,1:系统默认)")
    private Boolean isDefault;

    @ApiModelProperty(value = "商品数量")
    private Long prodCount;

    @ApiModelProperty(value = "列表样式(0:一列一个,1:一列两个,2:一列三个)")
    private Integer style;

    @ApiModelProperty(value = "排序")
    private Integer seq;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;


}
