package com.zy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.entity.SysRole;
import com.zy.entity.SysRoleMenu;
import com.zy.entity.SysUser;
import com.zy.service.impl.SysRoleMenuService;
import com.zy.service.impl.SysRoleService;
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
@RequestMapping("/sys/role")
public class RoleController {
  @Autowired
  private SysRoleMenuService sysRoleMenuService;

  @Autowired
   private      SysRoleService  sysRoleService;


  @ApiOperation("全查询角色列表")
  @GetMapping("/list")
  public ResponseEntity<List<SysRole>> list(){
//    List<SysRoleMenu> list = sysRoleMenuService.list();
//    return ResponseEntity.ok(list);
    List<SysRole> list = sysRoleService.list();
    return  ResponseEntity.ok(list);
  }

  /**
   * 根据名称来做分页查询
   * @param page
   * @param roleName
   * @return
   */
  @GetMapping("/page")
  @RequiresPermissions("sys:role:page")
  @ApiOperation("分页查询角色数据")
  public  ResponseEntity<IPage<SysRole>> page(Page<SysRole> page,SysRole sysRole){
    IPage<SysRole> sysRolePage=sysRoleMenuService.findByPage(page,sysRole);
    return  ResponseEntity.ok(sysRolePage);
  }

  @DeleteMapping
  @ApiOperation("删除角色")
  @RequiresPermissions("sys:role:delete")
  public  ResponseEntity<Void> delete(@RequestBody List<Long> ids)
  {
    sysRoleService.removeByIds(ids);
    return  ResponseEntity.ok().build();
  }
  @PostMapping
  @ApiModelProperty("新增一个角色")
  @RequiresPermissions("sys:role:save")
  public ResponseEntity<Void> add(@RequestBody @Validated SysRole sysRole){
    SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
    sysRole.setCreateUserId(sysUser.getUserId());
    sysRoleService.save(sysRole);
    return  ResponseEntity.ok().build();
  }

  @ApiModelProperty("查询一个角色")
  @GetMapping("/info/{id}")
  @RequiresPermissions("sys:role:info")
  public  ResponseEntity<SysRole> findById(@PathVariable("id") Long id){
    SysRole byId = sysRoleService.getById(id);
    return  ResponseEntity.ok(byId);
  }

  @PutMapping
  @ApiModelProperty("修改角色数据")
  @RequiresPermissions("sys:role:update")
  public  ResponseEntity<Void> update(@RequestBody @Validated SysRole sysRole){
    sysRoleService.updateById(sysRole);
    return  ResponseEntity.ok().build();
  }
}
