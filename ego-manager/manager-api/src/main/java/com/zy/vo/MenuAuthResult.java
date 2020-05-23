package com.zy.vo;

import com.zy.entity.SysMenu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MenuAuthResult  {
  @ApiModelProperty("菜单数据")
  private List<SysMenu> menuList;

  @ApiModelProperty("所有的权限数据")
  private  List<Element> authorities;
}
