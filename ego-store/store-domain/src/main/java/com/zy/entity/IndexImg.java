package com.zy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 主页轮播图
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="IndexImg对象", description="主页轮播图")
public class IndexImg implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "img_id", type = IdType.AUTO)
    private Long imgId;

    @ApiModelProperty(value = "店铺ID")
    private Long shopId;

    @ApiModelProperty(value = "图片")
    @NotBlank
    private String imgUrl;

    @ApiModelProperty(value = "说明文字,描述")
    private String des;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "链接")
    private String link;

    @ApiModelProperty(value = "状态")
    private Byte status;

    @ApiModelProperty(value = "顺序")
    private Integer seq;

    @ApiModelProperty(value = "上传时间")
    private Date uploadTime;

    @ApiModelProperty(value = "关联")
    private Long relation;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @TableField(exist = false)
    @ApiModelProperty("轮播图关联的商品图片")
    private  String pic;

    @TableField(exist = false)
    @ApiModelProperty("轮播图关联的商品的名称")
    private  String prodName;


}
