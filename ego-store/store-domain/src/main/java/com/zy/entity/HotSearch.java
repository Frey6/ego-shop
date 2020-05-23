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

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 热搜
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HotSearch对象", description="热搜")
public class HotSearch implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "hot_search_id", type = IdType.AUTO)
    private Long hotSearchId;

    @ApiModelProperty(value = "店铺ID 0为全局热搜")
    private Long shopId;

    @ApiModelProperty(value = "内容")
    @NotBlank
    private String content;

    @ApiModelProperty(value = "录入时间")
    private Date recDate;

    @ApiModelProperty(value = "顺序")
    private Integer seq;

    @ApiModelProperty(value = "状态 0下线 1上线")
    private Integer status;

    @ApiModelProperty(value = "热搜标题")
    @NotBlank
    private String title;


}
