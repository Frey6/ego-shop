package com.zy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zy.anno.Log;
import com.zy.entity.SysUser;
import com.zy.service.impl.SysUserService;
import com.zy.utils.HashMD5Util;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/user")
public class UserController {

  @Autowired
  private SysUserService sysUserService ;

  @GetMapping("/info")
  public ResponseEntity<SysUser> getCurrentUser(){
    // 从shiro 里面获取当前的登录对象
    SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
    return ResponseEntity.ok(sysUser) ;
  }
  /**
   * 分页查询
   */
  @GetMapping("/page")
  @ApiOperation("分页的查询")
  @RequiresPermissions(value = {"sys:user:page"})
  public ResponseEntity<IPage<SysUser>> findByPage(Page<SysUser> page,SysUser sysUser){
//    IPage<SysUser> page = sysUserService.page(new Page<>(current, size));
    IPage<SysUser> sysUserIPage=  sysUserService.findByPage(page,sysUser);
    return ResponseEntity.ok(sysUserIPage) ;
  }

  /**
   * 删除单个值
   * @param id
   * @return
   */
  @DeleteMapping("/{id}")
  @ApiOperation("删除单个值")
  @RequiresPermissions(value ={"sys:user:delete"})
  public ResponseEntity<Void> delete(@PathVariable("id")Long id){
    sysUserService.removeById(id);
    return  ResponseEntity.ok().build() ;
  }

  /**
   * 删除多个值
   */
  @DeleteMapping
  @ApiOperation("删除多个值")
  @RequiresPermissions(value = {"sys:user:info"})
  @Log(operation = "删除一个用户")
  public ResponseEntity<Void> delete(@RequestBody List<Long> ids){
    sysUserService.removeByIds(ids);
    return  ResponseEntity.ok().build() ;
  }

  /**
   * 回显一个值
   */
  @GetMapping("/info/{id}")
  @ApiOperation("回显一个值")
  public ResponseEntity<SysUser> findById(@PathVariable("id") Long id){
    SysUser sysUser = sysUserService.getById(id);
    return ResponseEntity.ok(sysUser) ;
  }

  /**
   * 新增一个值
   * @param sysUser
   * @return
   */
  @PostMapping
  @ApiOperation("新增一个值")
  @RequiresPermissions(value = {"sys:user:save"})
  public ResponseEntity<Void> add(@RequestBody SysUser sysUser){
    sysUser.setPassword(HashMD5Util.getMd5(sysUser.getPassword(),2));
    SysUser currentUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
    sysUser.setCreateUserId(currentUser.getUserId());
    sysUserService.save(sysUser);
    return  ResponseEntity.ok().build() ;
  }

  /**
   * 修改一个值
   * @param sysUser
   * @return
   */
  @PutMapping
  @ApiOperation("修改一个值")
  @RequiresPermissions(value = {"sys:user:update"})
  public ResponseEntity<Void> update(@RequestBody @Validated SysUser sysUser){
    sysUser.setPassword(HashMD5Util.getMd5(sysUser.getPassword(),2));
    sysUserService.updateById(sysUser);
    return ResponseEntity.ok().build() ;
  }
}

