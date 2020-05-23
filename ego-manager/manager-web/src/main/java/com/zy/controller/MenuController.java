package com.zy.controller;

import com.zy.entity.SysMenu;
import com.zy.entity.SysUser;
import com.zy.service.impl.SysMenuService;
import com.zy.vo.MenuAuthResult;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/menu")
public class MenuController {
  @Autowired
  private SysMenuService sysMenuService;

  /**
   * 加载用户的菜单和权限数据
   * @return
   */
  @GetMapping("/nav")
  public ResponseEntity<MenuAuthResult> loadMenuAndAuth(){
    SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
    MenuAuthResult menuAuthResult = sysMenuService.loadMenuAndAuth(sysUser.getUserId());
    return  ResponseEntity.ok(menuAuthResult);

  }
  @GetMapping("/table")
  @ApiOperation("加载所有的菜单列表")
  @RequiresPermissions(value = "sys:menu:list")
  public ResponseEntity<List<SysMenu>> loadAllMenuList(){
    List<SysMenu> list = sysMenuService.list();
    return  ResponseEntity.ok(list);
  }

  @GetMapping("/list")
  @ApiOperation("查询父菜单")
  @RequiresPermissions("sys:menu:list")
  public  ResponseEntity<List<SysMenu>> loadParentMenuList(){
    List<SysMenu> sysMenus = sysMenuService.listParentList();
    return  ResponseEntity.ok(sysMenus);
  }
  @ApiOperation("新增一个菜单")
  @PostMapping
  public  ResponseEntity<Void> add(@RequestBody @Validated SysMenu sysMenu){
     sysMenuService.save(sysMenu);
     return  ResponseEntity.ok().build();

  }
  @ApiOperation("删除一个菜单")
  @RequiresPermissions("sys:menu:delete")
  @DeleteMapping("/{id}")
  public  ResponseEntity<Void> delete(@PathVariable("id") Long id){
    sysMenuService.removeById(id);
    return  ResponseEntity.ok().build();
  }
  @ApiOperation("数据的回显")
  @RequiresPermissions("sys:menu:info")
  @GetMapping("/info/{id}")
  public  ResponseEntity<SysMenu> findById(@PathVariable("id") Long id){
    SysMenu byId = sysMenuService.getById(id);
    return  ResponseEntity.ok(byId);
  }
  @PutMapping
  @ApiOperation("修改数据")
  @RequiresPermissions("sys:menu:update")
  public  ResponseEntity<Void> update(@RequestBody @Validated SysMenu sysMenu){
    sysMenuService.updateById(sysMenu);
    return  ResponseEntity.ok().build();

  }

}
