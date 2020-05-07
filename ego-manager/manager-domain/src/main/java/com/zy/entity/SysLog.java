package com.zy.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统日志
 * </p>
 *
 * @author zy
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysLog对象", description="系统日志")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;
//    public  void builder() {
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;

        @ApiModelProperty(value = "用户名")
        private String username;

        @ApiModelProperty(value = "用户操作")
        private String operation;

        @ApiModelProperty(value = "请求方法")
        private String method;

        @ApiModelProperty(value = "请求参数")
        private String params;

        @ApiModelProperty(value = "执行时长(毫秒)")
        private Long time;

        @ApiModelProperty(value = "IP地址")
        private String ip;

        @ApiModelProperty(value = "创建时间")
        private Date createDate;



//    public SysLog(Builder builder) {
//        this.id = id;
//        this.username = username;
//        this.operation = operation;
//        this.method = method;
//        this.params = params;
//        this.time = time;
//        this.ip = ip;
//        this.createDate = createDate;
//    }
//    }

//        public void build() {
//        }


//  public  SysLog build(){
//        return  new SysLog();
//  }



}
